const sharedConfig = require("@repo/tailwind-config");

/**
 * @type {Pick<import('tailwindcss').Config, "presets" | "content">}
 */
const config = {
  content: [
    "./app/**/*.tsx",
    "../../libs/ui/src/**/*.tsx",
    "../../libs/profile/components/**/*.tsx",
    "../../libs/services/components/**/*.tsx",
  ],
  presets: [sharedConfig],
};

module.exports = config;
