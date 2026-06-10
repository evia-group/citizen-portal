export default (api) => {
  api.cache(true);
  return {
    presets: [["babel-preset-expo", { jsxImportSource: "nativewind" }]],
    plugins: [
      "@babel/plugin-proposal-export-namespace-from",
      // Inlined replacement for the "nativewind/babel" preset:
      // react-native-css-interop@0.2.x hard-codes "react-native-worklets/plugin"
      // (Reanimated 4 / RN 0.83+ only). This stack is on Reanimated 3, so we use
      // the same css-interop plugins but with "react-native-reanimated/plugin".
      "react-native-css-interop/dist/babel-plugin",
      [
        "@babel/plugin-transform-react-jsx",
        {
          runtime: "automatic",
          importSource: "react-native-css-interop",
        },
      ],
      "react-native-reanimated/plugin",
    ],
  };
};
