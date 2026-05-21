import {
  type UndefinedInitialDataOptions,
  type UseMutationOptions,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import type {
  DogApplication,
  DogApplicationFormData,
} from "../models/dog-application";
import {
  createDogApplication,
  getDogApplication,
  getDogApplications,
} from "../services/dog-application";

const queryKeys = {
  dogApplication: "dog-application",
};

/**
 * Query hook to get a specific dog application by ID.
 * @param id Dog Application ID
 * @param options `useQuery` options
 */
export const useGetDogApplication = (
  id: DogApplication["id"],
  options?: Omit<
    UndefinedInitialDataOptions<DogApplication>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<DogApplication>({
    queryKey: [queryKeys.dogApplication, id],
    queryFn: () => getDogApplication(id),
    ...options,
  });
};

/**
 * Query hook to get a list of dog applications
 * @param options `useQuery` options
 */
export const useGetDogApplications = (
  options?: Omit<
    UndefinedInitialDataOptions<DogApplication[]>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<DogApplication[]>({
    queryKey: [queryKeys.dogApplication, "list"],
    queryFn: () => getDogApplications(),
    ...options,
  });
};

/**
 * Mutation hook to create a dog application.
 */
export const useCreateDogApplication = (
  options?: Omit<
    UseMutationOptions<DogApplication, Error, DogApplicationFormData>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (dogApplication: DogApplicationFormData) =>
      createDogApplication(dogApplication),
    onSuccess: () => {
      // Invalidate the profile query to refetch the data
      queryClient.invalidateQueries({ queryKey: [queryKeys.dogApplication] });
    },
    ...options,
  });
};
