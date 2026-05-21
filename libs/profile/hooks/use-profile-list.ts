import type { SearchParamsOption } from "@repo/shared";
import {
  type UndefinedInitialDataOptions,
  useQuery,
} from "@tanstack/react-query";
import type { ProfileList } from "../models/profile";
import { getProfileList } from "../services/profile";

export const useProfileList = (
  params?: SearchParamsOption,
  options?: Omit<
    UndefinedInitialDataOptions<ProfileList>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<ProfileList>({
    queryKey: ["profile", "list", params],
    queryFn: () => getProfileList(params),
    ...options,
  });
};
