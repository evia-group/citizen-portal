import { getMyProfile } from "@repo/profile";
import { registerAuthAdapter, unregisterAuthAdapter } from "@repo/shared";
import {
  type AuthRequestConfig,
  exchangeCodeAsync,
  makeRedirectUri,
  revokeAsync,
  type TokenResponse,
  useAuthRequest,
  useAutoDiscovery,
} from "expo-auth-session";
import * as WebBrowser from "expo-web-browser";
import { useEffect, useRef, useState } from "react";
import {
  AuthenticationContext,
  type AuthenticationContextType,
} from "./authentication-context";
import {
  getItem as getItemFromStorage,
  setItem as setItemInStorage,
} from "./storage";

// Possibly completes an authentication session on web in a window popup. The method should be invoked on the page that the window redirects to.
WebBrowser.maybeCompleteAuthSession();

export function AuthenticationProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const discovery = useAutoDiscovery(
    `${
      process.env.EXPO_PUBLIC_KEYCLOAK_URL ??
      process.env.NEXT_PUBLIC_KEYCLOAK_URL
    }/realms/portal`,
  );

  const [authState, setAuthState] = useState<
    Omit<AuthenticationContextType, "login" | "logout">
  >({
    isReady: false,
    isAuthenticated: false,
    isLoggingIn: false,
    loginError: null,
    token: null,
    profileId: null,
  });

  // tokenRef lets the adapter callbacks always see the latest token without
  // re-registering the adapter on every token update (fixes the stale-closure bug).
  const tokenRef = useRef<TokenResponse | null>(null);

  // Create and load an auth request
  const authRequestConfig = {
    clientId: "user-portal",
    redirectUri: makeRedirectUri({
      scheme: "userportal",
      native: "userportal://auth/redirect",
    }),
    scopes: ["openid", "profile", "user-portal-scope"],
  } satisfies AuthRequestConfig;
  const [request, _result, promptAsync] = useAuthRequest(
    authRequestConfig,
    discovery,
  );

  // Keep tokenRef in sync with authState.token on every token update.
  useEffect(() => {
    tokenRef.current = authState.token;
  }, [authState.token]);

  useEffect(() => {
    setAuthState((prev) => ({ ...prev, isReady: !!request }));
  }, [request]);

  // Register the auth adapter once discovery is ready; unregister on cleanup.
  // authRequestConfig is a stable literal defined in the same component body;
  // adding it to deps would cause spurious re-registrations on every render.
  // biome-ignore lint/correctness/useExhaustiveDependencies: authRequestConfig is a stable literal; discovery is the real trigger
  useEffect(() => {
    if (!discovery) return;

    registerAuthAdapter({
      getAccessToken: () => tokenRef.current?.accessToken ?? null,

      isExpiringSoon: () => {
        const t = tokenRef.current;
        if (!t) return false;
        // Fixed 30s margin — do NOT use t.shouldRefresh() (default secondsMargin=0 is the original bug).
        const expiresIn = t.expiresIn ?? 300; // default to realm's 300s if not set
        const now = Date.now() / 1000;
        return now >= t.issuedAt + expiresIn - 30;
      },

      refresh: async () => {
        const startToken = tokenRef.current;
        if (!startToken || !discovery) return null;

        try {
          const refreshed = await startToken.refreshAsync(
            authRequestConfig,
            discovery,
          );

          // Generation guard: if the user logged out (or re-logged-in) while the
          // refresh was in flight, discard the result and don't mutate state.
          if (tokenRef.current !== startToken) return null;

          if (refreshed) {
            setItemInStorage(refreshed);
            setAuthState((prev) => ({ ...prev, token: refreshed }));
            return refreshed.accessToken;
          }
          return null;
        } catch (e) {
          // Same generation guard in the error branch.
          if (tokenRef.current !== startToken) return null;
          throw e;
        }
      },

      onRefreshFailed: () => onSessionInvalidated(),
    });

    return () => {
      unregisterAuthAdapter();
    };
  }, [discovery]);

  // Effect B (split): only fetch profile on auth-state transitions, not on
  // every refresh-induced token swap.
  useEffect(() => {
    function reset() {
      setAuthState((prev) => ({
        ...prev,
        isAuthenticated: false,
        isLoggingIn: false,
        isReady: true,
        profileId: null,
      }));
    }

    if (!authState.token) {
      reset();
      return;
    }

    if (authState.profileId) return;

    (async () => {
      // Retry the initial profile fetch a few times: right after a Keycloak
      // login the backend may not have provisioned the user record yet, so
      // /me returns 401 (mapped to null) on the first attempt and succeeds
      // shortly after.
      const delays = [0, 250, 750, 1500];
      let profile: Awaited<ReturnType<typeof getMyProfile>> = null;
      for (const ms of delays) {
        if (ms > 0) await new Promise((r) => setTimeout(r, ms));
        profile = await getMyProfile();
        if (profile) break;
      }
      if (profile) {
        setAuthState((prev) => ({
          ...prev,
          isAuthenticated: true,
          isLoggingIn: false,
          isReady: true,
          profileId: profile.id,
        }));
      } else {
        reset();
      }
    })();
  }, [authState.token, authState.profileId]);

  // Restore token from storage on mount.
  useEffect(() => {
    (async () => {
      const token = await getItemFromStorage();
      if (token) {
        setAuthState((prev) => ({
          ...prev,
          isReady: true,
          isAuthenticated: true,
          isLoggingIn: false,
          token,
        }));
      }
    })();
  }, []);

  if (!discovery) {
    return null;
  }

  /**
   * Clears local session state and unregisters the adapter.
   * Called when a refresh fails — does NOT revoke the access token because it
   * is already invalid, and the round-trip would be futile while the user is
   * on a broken page.
   */
  function onSessionInvalidated() {
    setItemInStorage(null);
    setAuthState((prev) => ({
      ...prev,
      token: null,
    }));
    unregisterAuthAdapter();
  }

  async function logout() {
    if (authState.token && discovery) {
      try {
        await revokeAsync(
          {
            token: authState.token?.accessToken,
            clientId: authRequestConfig.clientId,
          },
          discovery,
        );
      } catch (error) {
        console.error("Failed to revoke token", error);
      }
    }

    setItemInStorage(null);
    setAuthState((prev) => ({
      ...prev,
      token: null,
    }));
    // Unregister adapter on explicit logout; reset is an internal operation
    // and doesn't need to unregister.
    unregisterAuthAdapter();
  }

  async function login() {
    try {
      setAuthState((prev) => ({
        ...prev,
        isLoggingIn: true,
        loginError: null,
      }));
      const response = await promptAsync();
      if (response.type === "success" && discovery) {
        const { code } = response.params;
        const extraParams = request?.codeVerifier
          ? {
              code_verifier: request?.codeVerifier,
            }
          : undefined;

        const token = await exchangeCodeAsync(
          {
            ...authRequestConfig,
            code,
            extraParams: { ...extraParams },
          },
          discovery,
        );
        if (token) {
          setItemInStorage(token);
          setAuthState((prev) => ({
            ...prev,
            token,
          }));
        }
      } else {
        // Covers: "error", "cancel", "dismiss", "locked", "opened", and any
        // future result types that are not "success". For error results show a
        // German error message; for all other non-success results (including
        // "opened", which means the browser window opened but the user has not
        // yet acted) just clear the spinner without an error.
        setAuthState((prev) => ({
          ...prev,
          isLoggingIn: false,
          loginError:
            response.type === "error"
              ? "Anmeldung fehlgeschlagen. Bitte versuchen Sie es erneut."
              : null,
        }));
      }
    } catch (error) {
      console.error("Failed to log in", error);
      setAuthState((prev) => ({
        ...prev,
        isLoggingIn: false,
        loginError: "Anmeldung fehlgeschlagen. Bitte versuchen Sie es erneut.",
      }));
      await logout();
    }
  }

  return (
    <AuthenticationContext.Provider
      value={
        {
          ...authState,
          login,
          logout,
        } as AuthenticationContextType
      }
    >
      {children}
    </AuthenticationContext.Provider>
  );
}
