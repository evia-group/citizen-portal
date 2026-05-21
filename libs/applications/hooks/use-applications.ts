import type { DogApplication } from "@repo/services";
import type { SearchParamsOption } from "@repo/shared";
import {
  type UndefinedInitialDataOptions,
  type UseMutationOptions,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import type { Application } from "../models/application";
import {
  getApplicationById,
  getApplicationsList,
  getDogApplicationByApplicationId,
  updateApplication,
} from "../services/applications";

export const useApplicationList = (
  params?: SearchParamsOption,
  options?: Omit<
    UndefinedInitialDataOptions<Application[]>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<Application[]>({
    queryKey: ["applications", "list", params],
    queryFn: () => getApplicationsList(params),
    ...options,
  });
};

/**
 * Query hook to get a specific DogApplication by Application ID.
 * @param id Application ID
 * @param options `useQuery` options
 */
export const useDogApplication = (
  id: number,
  options?: Omit<
    UndefinedInitialDataOptions<DogApplication>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<DogApplication>({
    queryKey: ["dog-applications", id],
    queryFn: () => getDogApplicationByApplicationId(id),
    ...options,
  });
};

/**
 * Query hook to get a specific Application by  ID.
 * @param id Application ID
 * @param options `useQuery` options
 */
export const useApplication = (
  id: number,
  options?: Omit<
    UndefinedInitialDataOptions<Application>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<Application>({
    queryKey: ["applications", id],
    queryFn: () => getApplicationById(id),
    ...options,
  });
};

/**
 * Mutation hook to update an application.
 */
export const useUpdateApplication = (
  options?: Omit<
    UseMutationOptions<Application, Error, Application>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (application: Application) => updateApplication(application),
    onSuccess: () => {
      // Invalidate the profile query to refetch the data
      queryClient.invalidateQueries({ queryKey: ["applications"] });
    },
    ...options,
  });
};
