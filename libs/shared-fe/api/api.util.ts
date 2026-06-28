import ky, { HTTPError, type Input, type Options } from "ky";

type HttpMethod = "get" | "post" | "put" | "patch" | "delete";

export const basePath =
  process.env.EXPO_PUBLIC_API_URL ??
  process.env.NEXT_PUBLIC_API_URL ??
  "http://localhost:8888/api/v1";

export interface AuthAdapter {
  getAccessToken: () => string | null;
  isExpiringSoon: () => boolean;
  refresh: () => Promise<string | null>;
  onRefreshFailed: () => void;
}

let adapter: AuthAdapter | null = null;

// Dedup is per-tab; cross-tab refresh races are a known limitation — see BP-56.
let inflight: Promise<string | null> | null = null;

export function registerAuthAdapter(a: AuthAdapter): void {
  adapter = a;
  inflight = null;
}

export function unregisterAuthAdapter(): void {
  adapter = null;
  inflight = null;
}

export async function ensureFresh(force: boolean): Promise<string | null> {
  if (!adapter) return null;

  // Return the in-flight refresh promise to all concurrent callers (dedup).
  // This check MUST come before the short-circuit below.
  if (inflight !== null) return inflight;

  // Short-circuit: token is still valid and we are not forced to refresh.
  if (!force && !adapter.isExpiringSoon()) {
    return adapter.getAccessToken();
  }

  // Start a new refresh, share the promise for any concurrent callers.
  inflight = (async () => {
    try {
      return (await adapter?.refresh()) ?? null;
    } catch (e) {
      adapter?.onRefreshFailed();
      throw e;
    }
  })().finally(() => {
    inflight = null;
  });

  return inflight;
}

const fetcher = ky.create({
  prefixUrl: basePath,
  retry: {
    limit: 1,
    statusCodes: [401],
    methods: ["get", "post", "put", "patch", "delete"],
  },
  hooks: {
    beforeRequest: [
      async (request) => {
        if (!adapter) return;
        const token = await ensureFresh(false);
        if (token) {
          request.headers.set("Authorization", `Bearer ${token}`);
        }
      },
    ],
    beforeRetry: [
      async ({ request, error }) => {
        const status =
          error instanceof HTTPError ? error.response.status : undefined;
        if (status !== 401) throw error;

        if (!adapter) throw error;
        const token = await ensureFresh(true);
        if (!token) throw error;
        request.headers.set("Authorization", `Bearer ${token}`);
      },
    ],
  },
});

/**
 * Helper function to make a request with the fetcher
 * @param url URL or path to request
 * @param method HTTP method to use
 * @param options Options to pass to ky (additional headers, searchParams, etc.)
 */
export async function client<Response = unknown>(
  url: Input,
  method: HttpMethod,
  options?: Options,
) {
  const urlWithoutSlash = url.toString().replace(/^\//, "");

  try {
    const result = await fetcher[method](
      urlWithoutSlash,
      options,
    ).json<Response>();
    return result;
  } catch (error) {
    console.error("error fetching from API server", error);
    throw error;
  }
}
