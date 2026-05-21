export {
  registerAuthAdapter,
  unregisterAuthAdapter,
  client,
  basePath,
} from "./api/api.util";

export type { AuthAdapter } from "./api/api.util";

export type { SearchParamsOption, Options, Input } from "ky";
export { HTTPError } from "ky";
