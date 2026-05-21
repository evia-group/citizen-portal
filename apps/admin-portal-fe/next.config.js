// @ts-check

const path = require("node:path");

/** @type {import('next').NextConfig} */
module.exports = {
  assetPrefix: "./",
  reactStrictMode: true,
  output: "standalone",
  experimental: {
    outputFileTracingRoot: path.join(__dirname, "../../"),
  },
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
