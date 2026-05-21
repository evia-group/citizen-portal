import {
  type UndefinedInitialDataOptions,
  type UseMutationOptions,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import type { Notifications } from "../models/notifications";
import type { Profile, ProfileFormData } from "../models/profile";
import {
  createProfile,
  deleteProfile,
  getMyProfile,
  getProfile,
  getProfileNotification,
  getProfileNotifications,
  updateNotificationStatus,
  updateProfile,
} from "../services/profile";

/**
 * Query hook to get a specific profile by ID.
 * @param profileId Profile ID
 * @param options `useQuery` options
 */
export const useProfileNotifications = (
  profileId: Profile["id"],
  options?: Omit<
    UndefinedInitialDataOptions<Notifications[]>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<Notifications[]>({
    queryKey: ["notification", profileId],
    queryFn: () => getProfileNotifications(profileId),
    ...options,
  });
};

/**
 * Query hook to get a specific profile by ID.
 * @param profileId Profile ID
 * @param notificationId Notification ID
 * @param options `useQuery` options
 */
export const useProfileNotification = (
  notificationId: Notifications["id"],
  profileId: Profile["id"],
  options?: Omit<
    UndefinedInitialDataOptions<Notifications>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<Notifications>({
    queryKey: ["notification", profileId, notificationId],
    queryFn: () => getProfileNotification(notificationId, profileId),
    ...options,
  });
};

/**
 * Query hook to get specific profile notification by Profile ID.
 * @param id Profile ID
 * @param options `useQuery` options
 */
export const useProfile = (
  id: Profile["id"],
  options?: Omit<UndefinedInitialDataOptions<Profile>, "queryKey" | "queryFn">,
) => {
  return useQuery<Profile>({
    queryKey: ["profile", id],
    queryFn: () => getProfile(id),
    ...options,
  });
};

/**
 * Query hook to get my profile
 * @param options `useQuery` options
 */
export const useMyProfile = (
  options?: Omit<
    UndefinedInitialDataOptions<Profile | null>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<Profile | null>({
    queryKey: ["profile", "me"],
    queryFn: () => getMyProfile(),
    ...options,
  });
};

/**
 * Mutation hook to update a profile.
 * @param id Profile ID
 */
export const useUpdateProfile = (
  id: Profile["id"],
  options?: Omit<
    UseMutationOptions<Profile, Error, ProfileFormData>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (profile: ProfileFormData) => updateProfile(id, profile),
    onSuccess: () => {
      // Invalidate the profile query to refetch the data
      queryClient.invalidateQueries({ queryKey: ["profile"] });
    },
    ...options,
  });
};

/**
 * Query hook to get a specific profile by ID.
 * @param profileId Profile ID
 * @param notificationId Notification ID
 * @param options `useQuery` options
 */
export const useUpdateProfileNotification = (
  notificationId: Notifications["id"],
  profileId: Profile["id"],
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => updateNotificationStatus(notificationId, profileId),
    onSuccess: () => {
      // Invalidate the profile query to refetch the data
      queryClient.invalidateQueries({ queryKey: ["notification"] });
    },
  });
};

/**
 * Mutation hook to create a profile.
 */
export const useCreateProfile = (
  options?: Omit<
    UseMutationOptions<Profile, Error, ProfileFormData>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (profile: ProfileFormData) => createProfile(profile),
    onSuccess: () => {
      // Invalidate the profile query to refetch the data
      queryClient.invalidateQueries({ queryKey: ["profile"] });
    },
    ...options,
  });
};

/**
 * Mutation hook to delete a profile.
 */
export const useDeleteProfile = (
  id: Profile["id"],
  options?: Omit<
    UseMutationOptions<Profile, Error, Profile>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => deleteProfile(id),
    onSuccess: () => {
      // Invalidate the profile query to refetch the data
      queryClient.invalidateQueries({ queryKey: ["profile"] });
    },
    ...options,
  });
};
