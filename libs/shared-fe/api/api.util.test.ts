import {
  ensureFresh,
  registerAuthAdapter,
  unregisterAuthAdapter,
} from "./api.util";

function makeAdapter(overrides: {
  getAccessToken?: () => string | null;
  isExpiringSoon?: () => boolean;
  refresh?: () => Promise<string | null>;
  onRefreshFailed?: () => void;
}) {
  const adapter = {
    getAccessToken: jest.fn(() => "current-token"),
    isExpiringSoon: jest.fn(() => false),
    refresh: jest.fn(async () => "new-token"),
    onRefreshFailed: jest.fn(),
    ...overrides,
  };
  return adapter;
}

beforeEach(() => {
  unregisterAuthAdapter();
});

afterEach(() => {
  unregisterAuthAdapter();
});

describe("ensureFresh", () => {
  test("1. Dedup: 5 concurrent ensureFresh(false) when isExpiringSoon=true produce exactly 1 adapter.refresh() call; all resolve with the same value", async () => {
    let resolveRefresh!: (val: string) => void;
    const refreshPromise = new Promise<string>((res) => {
      resolveRefresh = res;
    });

    const adapter = makeAdapter({
      isExpiringSoon: () => true,
      refresh: jest.fn(() => refreshPromise),
    });
    registerAuthAdapter(adapter);

    // Fire 5 concurrent calls before resolving
    const calls = [
      ensureFresh(false),
      ensureFresh(false),
      ensureFresh(false),
      ensureFresh(false),
      ensureFresh(false),
    ];

    // Now resolve the single shared refresh
    resolveRefresh("deduped-token");

    const results = await Promise.all(calls);

    expect(adapter.refresh).toHaveBeenCalledTimes(1);
    expect(results).toEqual([
      "deduped-token",
      "deduped-token",
      "deduped-token",
      "deduped-token",
      "deduped-token",
    ]);
  });

  test("2. Force flag: ensureFresh(true) calls adapter.refresh() even when isExpiringSoon=false", async () => {
    const adapter = makeAdapter({
      isExpiringSoon: () => false,
      refresh: jest.fn(async () => "forced-token"),
    });
    registerAuthAdapter(adapter);

    const result = await ensureFresh(true);

    expect(adapter.refresh).toHaveBeenCalledTimes(1);
    expect(result).toBe("forced-token");
  });

  test("3. Short-circuit: ensureFresh(false) when isExpiringSoon=false returns current token without calling refresh()", async () => {
    const adapter = makeAdapter({
      isExpiringSoon: () => false,
      getAccessToken: () => "stable-token",
    });
    registerAuthAdapter(adapter);

    const result = await ensureFresh(false);

    expect(adapter.refresh).not.toHaveBeenCalled();
    expect(result).toBe("stable-token");
  });

  test("4. In-flight reset: after refresh resolves, subsequent ensureFresh(true) starts a new refresh", async () => {
    const adapter = makeAdapter({
      isExpiringSoon: () => false,
      refresh: jest.fn(async () => "fresh-token"),
    });
    registerAuthAdapter(adapter);

    // First forced refresh
    await ensureFresh(true);
    expect(adapter.refresh).toHaveBeenCalledTimes(1);

    // Second forced refresh — inflight is null now, so a new one should start
    await ensureFresh(true);
    expect(adapter.refresh).toHaveBeenCalledTimes(2);
  });

  test("5. Failure path: when adapter.refresh() throws, adapter.onRefreshFailed() is called once and error propagates", async () => {
    const refreshError = new Error("Keycloak refresh failed");
    const adapter = makeAdapter({
      isExpiringSoon: () => true,
      refresh: jest.fn(async () => {
        throw refreshError;
      }),
    });
    registerAuthAdapter(adapter);

    await expect(ensureFresh(false)).rejects.toThrow("Keycloak refresh failed");
    expect(adapter.onRefreshFailed).toHaveBeenCalledTimes(1);
  });

  test("no adapter registered: returns null without throwing", async () => {
    unregisterAuthAdapter();
    const result = await ensureFresh(false);
    expect(result).toBeNull();
  });
});
