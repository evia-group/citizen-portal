/** @type {import('jest').Config} */
module.exports = {
  testEnvironment: "node",
  transform: {
    "^.+\\.tsx?$": [
      "babel-jest",
      {
        presets: [
          ["@babel/preset-env", { targets: { node: "current" } }],
          "@babel/preset-typescript",
        ],
      },
    ],
    // Transform ESM-only packages from node_modules (e.g. ky)
    "^.+\\.js$": [
      "babel-jest",
      {
        presets: [["@babel/preset-env", { targets: { node: "current" } }]],
      },
    ],
  },
  // Allow transforming ESM-only node_modules (ky publishes ESM-only)
  transformIgnorePatterns: ["node_modules/(?!(ky)/)"],
  testMatch: ["**/*.test.ts", "**/*.test.tsx"],
};
