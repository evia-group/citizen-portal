import type { SearchParamsOption } from "@repo/shared";
import {
  type UndefinedInitialDataOptions,
  useQuery,
} from "@tanstack/react-query";
import type { Domain } from "../models/services";
import { getServicesList } from "../services/services";

export const useServiceList = (
  params?: SearchParamsOption,
  options?: Omit<UndefinedInitialDataOptions<Domain[]>, "queryKey" | "queryFn">,
) => {
  return useQuery<Domain[]>({
    queryKey: ["services", "list", params],
    queryFn: () => getServicesList(params),
    ...options,
  });
};
