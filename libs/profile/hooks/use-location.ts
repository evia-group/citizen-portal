import {
  type UndefinedInitialDataOptions,
  useQuery,
} from "@tanstack/react-query";
import type { Location } from "../models/location";
import { getLocationList } from "../services/location";

/**
 * Query hook to get a list of locations.
 * @param options `useQuery` options
 */
export const useLocationList = (
  options?: Omit<
    UndefinedInitialDataOptions<Location[]>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<Location[]>({
    queryKey: ["locations"],
    queryFn: () => getLocationList(),
    ...options,
  });
};
