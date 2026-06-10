import { client, type SearchParamsOption } from "@repo/shared";
import type { Location } from "../models/location";

/**
 * Get a list of locations.
 */
export function getLocationList(searchParams?: SearchParamsOption) {
  return client<Location[]>("locations", "get", { searchParams });
}
