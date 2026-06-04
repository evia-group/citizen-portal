// Learn more https://docs.expo.io/guides/customizing-metro
// Learn more: https://docs.expo.dev/guides/monorepos/

const { getDefaultConfig } = require("expo/metro-config");
const { FileStore } = require("metro-cache");
const { withNativeWind } = require("nativewind/metro");
const path = require("node:path");
const { copyFileSync, existsSync, mkdirSync } = require("node:fs");
const { spawnSync } = require("node:child_process");
const exclusionList = require("metro-config/src/defaults/exclusionList");

// The `input` option passed to withNativeWind — NativeWind uses its basename
// as the cache filename, so the copy logic must derive names from this value.
const NATIVEWIND_INPUT = "../../libs/tailwind-config/global.css";

module.exports = withTurborepoManagedCache(
  withNoHmrOverlay(
    withMonorepoPaths(
      withNativeWindWebCssFix(
        withNativeWind(getDefaultConfig(__dirname), {
          input: NATIVEWIND_INPUT,
          configPath: "./tailwind.config.ts",
        }),
        NATIVEWIND_INPUT,
      ),
    ),
  ),
);

/**
 * Replaces the `LoadingView` module used by `@expo/metro-runtime` with a
 * no-op shim when the `EXPO_NO_HMR_OVERLAY` environment variable is set.
 * This suppresses the "Refreshing..." overlay that Metro injects during Hot
 * Module Replacement, which would otherwise block content during e2e testing.
 *
 * The predicate matches based on `context.originModulePath` (the file doing
 * the importing) combined with the resolved module name. Consumers inside
 * `@expo/metro-runtime` import `LoadingView` relatively (e.g. `./LoadingView`
 * from `HMRClient.js`), not via the absolute package path. Checking
 * `originModulePath` includes `@expo/metro-runtime` AND `moduleName` ends with
 * `LoadingView` reliably intercepts these relative requires.
 *
 * The change is opt-in: without the env var the resolver is untouched and
 * Fast Refresh (including the visual banner) works as normal.
 *
 * @param {import('expo/metro-config').MetroConfig} config
 * @returns {import('expo/metro-config').MetroConfig}
 */
function withNoHmrOverlay(config) {
  if (!process.env.EXPO_NO_HMR_OVERLAY) return config;
  const shimPath = path.resolve(__dirname, "shims/loading-view.noop.js");
  const originalResolve = config.resolver?.resolveRequest;
  config.resolver = config.resolver || {};
  config.resolver.resolveRequest = (context, moduleName, platform) => {
    // Only intercept on web — native platforms should keep the real HMR banner.
    if (platform !== "web") {
      if (originalResolve)
        return originalResolve(context, moduleName, platform);
      return context.resolveRequest(context, moduleName, platform);
    }
    if (
      context.originModulePath.includes("@expo/metro-runtime") &&
      moduleName.endsWith("LoadingView")
    ) {
      return { filePath: shimPath, type: "sourceFile" };
    }
    if (originalResolve) return originalResolve(context, moduleName, platform);
    return context.resolveRequest(context, moduleName, platform);
  };
  return config;
}

/**
 * Fixes NativeWind web CSS not rendering on `expo export -p web`.
 *
 * Root cause: NativeWind's metro transformer (for web) intercepts
 * `import '@repo/tailwind-config/global.css'` and replaces it with
 * `require('<cache>/global.css')`. But NativeWind's own tailwindCli writes
 * the generated CSS to `global.css.web.css`, never to `global.css`. Metro
 * therefore bundles an empty placeholder → no styles on web.
 *
 * Fix: at config-construction time (synchronously, before any Metro worker
 * spawns) ensure `<basename>` contains the real generated CSS by:
 *   1. If `<basename>.web.css` already exists (warm cache), copy it.
 *   2. Else, synchronously invoke the tailwindcss CLI to generate the CSS into
 *      `<basename>.web.css`, then copy it.
 *   3. If generation fails for any reason: always throw loudly with a visible
 *      multi-line banner so the developer is never silently served an empty CSS
 *      file. In CI/EXPO_NONINTERACTIVE the error message is more terse; in dev
 *      mode a diagnostic banner is printed before throwing so Metro startup
 *      fails visibly rather than producing an unstyled app.
 *
 * The `nativewindInput` param must match the `input` option given to
 * `withNativeWind` — NativeWind derives cache filenames from `path.basename(input)`.
 *
 * @param {import('expo/metro-config').MetroConfig} config
 * @param {string} nativewindInput - The `input` path passed to `withNativeWind`
 * @returns {import('expo/metro-config').MetroConfig}
 */
function withNativeWindWebCssFix(config, nativewindInput) {
  // TODO(BP-22): Remove this workaround once NativeWind fixes the upstream bug where
  // global.css.web.css is not resolved as a platform-suffix variant of global.css on web exports.
  // Track: https://github.com/nativewind/nativewind/issues/1492
  const cssBasename = path.basename(nativewindInput); // e.g. "global.css"
  const nativewindCacheDir = path.join(
    __dirname,
    "node_modules/.cache/nativewind",
  );
  const webCssPath = path.join(nativewindCacheDir, `${cssBasename}.web.css`);
  const globalCssPath = path.join(nativewindCacheDir, cssBasename);

  // Run at config-construction time so the CSS file is ready before any Metro worker spawns.
  try {
    mkdirSync(nativewindCacheDir, { recursive: true });

    if (!existsSync(webCssPath)) {
      // Cold cache: synchronously generate the real CSS via the tailwindcss CLI so
      // Metro reads real styles from the very first transform. Without this, NativeWind's
      // own (async) tailwindCli only writes `<basename>.web.css` after Metro has already
      // bundled `<basename>` — leaving the production export with empty styles.
      const inputPath = path.resolve(__dirname, nativewindInput);
      const tailwindBin = path.resolve(
        __dirname,
        "../../node_modules/.bin/tailwindcss",
      );
      const result = spawnSync(
        tailwindBin,
        ["-i", inputPath, "-o", webCssPath, "--config", "./tailwind.config.ts"],
        { cwd: __dirname, stdio: "pipe", encoding: "utf8" },
      );
      if (result.status !== 0) {
        throw new Error(
          `tailwindcss CLI exited with status ${result.status}: ${
            result.stderr || result.stdout || "(no output)"
          }`,
        );
      }
    }

    // Warm cache (or just-generated): copy the real CSS into place for Metro to bundle.
    copyFileSync(webCssPath, globalCssPath);
  } catch (err) {
    // In CI or any non-interactive build context (e.g. `expo export` inside Docker),
    // there is no second chance: shipping an empty CSS file silently breaks all styling.
    // Throw loudly so the build fails fast rather than producing a broken artefact.
    // In interactive dev (`expo start --web`), fall back softly — the next hot-reload
    // will re-run this step and recover.
    // Rely only on explicit CI/non-interactive signals — the TTY heuristic
    // was a false-positive when stdout was piped (e.g. `npm run dev | tee log`).
    const isBuildContext = process.env.CI || process.env.EXPO_NONINTERACTIVE;
    if (isBuildContext) {
      throw new Error(
        `[withNativeWindWebCssFix] Failed to prepare NativeWind CSS cache; refusing to ship empty styles.\n  src:  ${webCssPath}\n  dest: ${globalCssPath}\n  ${err.message}`,
      );
    }
    // In dev mode (expo start) a silent empty-CSS fallback means the developer
    // sees an unstyled app with no indication of what went wrong.  Throw loudly
    // instead so Metro startup fails with a visible red banner rather than
    // quietly serving empty styles.
    const banner = [
      "",
      "╔══════════════════════════════════════════════════════════════╗",
      "║  [withNativeWindWebCssFix] TAILWIND CSS GENERATION FAILED   ║",
      "║  Metro will NOT start until this is resolved.               ║",
      "╚══════════════════════════════════════════════════════════════╝",
      `  src:  ${webCssPath}`,
      `  dest: ${globalCssPath}`,
      `  ${err.message}`,
      "",
      "  Fix: ensure tailwindcss is installed and the input CSS path is correct.",
      "  Run: npx tailwindcss -i <input> -o <output> --config ./tailwind.config.ts",
      "",
    ].join("\n");
    throw new Error(banner);
  }

  return config;
}

/**
 * Add the monorepo paths to the Metro config.
 * This allows Metro to resolve modules from the monorepo.
 *
 * @see https://docs.expo.dev/guides/monorepos/#modify-the-metro-config
 * @param {import('expo/metro-config').MetroConfig} config
 * @returns {import('expo/metro-config').MetroConfig}
 */
function withMonorepoPaths(config) {
  const projectRoot = __dirname;
  const workspaceRoot = path.resolve(projectRoot, "../..");

  // #1 - Watch all files in the monorepo
  config.watchFolders = [workspaceRoot];

  // Exclude directories that contain non-source files that change during
  // e2e testing (playwright-cli snapshot YAML files, screenshots, worktree
  // state). Without this exclusion, Metro Fast Refresh triggers repeatedly
  // while e2e tests are running, surfacing the "Refreshing…" overlay and
  // blocking test assertions.
  //
  // Patterns are anchored to `workspaceRoot` so they only match the repo's
  // own `e2e/` and `.claude/` directories, not any same-named segment that
  // happens to appear higher in the absolute path. This matters when the
  // checkout itself lives inside a path containing `.claude/` (e.g. a
  // `.claude/worktrees/<TICKET>/` worktree) — an unanchored regex would
  // block every file in the project and Metro would 404 on entry.bundle.
  const escapeRegex = (s) => s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const rootPrefix = escapeRegex(workspaceRoot);
  config.resolver.blockList = exclusionList([
    // e2e scenario artefacts (.playwright-cli/ session snapshots, screenshots/)
    new RegExp(`^${rootPrefix}/e2e/.*`),
    // Git worktree state and Claude workspace files
    new RegExp(`^${rootPrefix}/\\.claude/.*`),
  ]);

  // #2 - Resolve modules within the project's `node_modules` first, then all monorepo modules
  config.resolver.nodeModulesPaths = [
    path.resolve(projectRoot, "node_modules"),
    path.resolve(workspaceRoot, "node_modules"),
  ];

  // #3 - Resolve "exports" in package.json correctly. Required so the workspace
  // libs (@repo/ui etc.), which declare their entry only via `exports`
  // (no `main`), resolve. SDK 53 / RN 0.79 enable this by default; we keep it on.
  // The @react-navigation ESM incompatibility is handled per-package in
  // `withReactNavigationExportsFix` rather than by disabling exports globally.
  config.resolver.unstable_enablePackageExports = true;

  // #4 - Resolve the `~` alias used inside @repo/ui (generated by shadcn/ui) to its src root.
  // tsconfig maps `~/*` → `libs/ui/src/*`; Metro doesn't read tsconfig paths, so we
  // replicate that mapping here via extraNodeModules.
  config.resolver.extraNodeModules = {
    "~": path.resolve(workspaceRoot, "libs/ui/src"),
  };

  return config;
}

/**
 * Move the Metro cache to the `node_modules/.cache/metro` folder.
 * This repository configured Turborepo to use this cache location as well.
 * If you have any environment variables, you can configure Turborepo to invalidate it when needed.
 *
 * @see https://turbo.build/repo/docs/reference/configuration#env
 * @param {import('expo/metro-config').MetroConfig} config
 * @returns {import('expo/metro-config').MetroConfig}
 */
function withTurborepoManagedCache(config) {
  config.cacheStores = [
    new FileStore({ root: path.join(__dirname, "node_modules/.cache/metro") }),
  ];
  return config;
}
