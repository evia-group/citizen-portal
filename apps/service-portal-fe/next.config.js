// @ts-check

const path = require("node:path");

/** @type {import('next').NextConfig} */
module.exports = {
  reactStrictMode: true,
  output: "standalone",
  outputFileTracingRoot: path.join(__dirname, "../../"),
  transpilePackages: ["nativewind", "react-native-css-interop"],
  basePath: process.env.NEXT_PUBLIC_BASE_PATH || "",
  webpack: (config) => {
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
