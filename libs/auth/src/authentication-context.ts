import type { Profile } from "@repo/profile";
import type { TokenResponse } from "expo-auth-session";
import { createContext } from "react";

type Basics = {
  isReady: boolean;
  isLoggingIn: boolean;
  loginError: string | null;
  login: () => Promise<void>;
  logout: () => Promise<void>;
};

export type LoggedIn = Basics & {
  isAuthenticated: true;
  token: TokenResponse;
  profileId: Profile["id"];
};

export type LoggedOut = Basics & {
  isAuthenticated: false;
  token: null;
  profileId: null;
};

export type AuthenticationContextType = LoggedIn | LoggedOut;

export const AuthenticationContext = createContext<AuthenticationContextType>({
  isReady: false,
  isLoggingIn: false,
  loginError: null,
  isAuthenticated: false,
  login() {
    throw new Error("login() not possible");
  },
  logout() {
    throw new Error("logout() not possible");
  },
  token: null,
  profileId: null,
} satisfies LoggedOut);
