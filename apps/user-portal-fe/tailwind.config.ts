import sharedConfig from "@repo/tailwind-config";
import type { Config } from "tailwindcss";

const config: Pick<Config, "content" | "presets"> = {
  content: [
    "./app/**/*.tsx",
    "./components/**/*.tsx",
    "../../libs/ui/src/**/*.tsx",
    "../../libs/profile/components/**/*.tsx",
    "../../libs/services/components/**/*.tsx",
  ],
  presets: [sharedConfig],
};

module.exports = config;
