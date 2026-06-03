// @ts-check

const path = require("node:path");

/** @type {import('next').NextConfig} */
module.exports = {
  assetPrefix: "./",
  reactStrictMode: true,
  output: "standalone",
  outputFileTracingRoot: path.join(__dirname, "../../"),
  transpilePackages: ["nativewind", "react-native-css-interop"],
  webpack: (config, { webpack }) => {
    // react-native ecosystem packages (e.g. react-native-reanimated, pulled in
    // via nativewind) reference the `__DEV__` global that Metro defines but
    // webpack does not. Define it so SSR/prerender doesn't throw ReferenceError.
    config.plugins.push(
      new webpack.DefinePlugin({
        __DEV__: JSON.stringify(process.env.NODE_ENV !== "production"),
      }),
    );
    config.resolve.alias = {
      ...config.resolve.alias,
      // Transform all direct `react-native` imports to `react-native-web`
      "react-native$": "react-native-web",
    };
    config.resolve.extensions = [
      ".web.js",
      ".web.jsx",
      ".web.ts",
      ".web.tsx",
      ...config.resolve.extensions,
    ];
    return config;
  },
};
