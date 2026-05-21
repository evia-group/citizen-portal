import { HTTPError, type SearchParamsOption, client } from "@repo/shared";
import type { Notifications } from "../models/notifications";
import type { Profile, ProfileFormData, ProfileList } from "../models/profile";

export async function getMyProfile() {
  try {
    return await client<Profile>("me", "get");
  } catch (error) {
    console.error("error fetching /me", error);
    if (error instanceof HTTPError && error.response.status === 401) {
      // Unauthorized is okay here, it just means the user is not logged in
      return null;
    }
    throw error;
  }
}

/**
 * Get a list of profiles.
 */
export function getProfileList(searchParams?: SearchParamsOption) {
  return client<ProfileList>("profiles", "get", { searchParams });
}

/**
 * Create a new profile.
 */
export function createProfile(json: ProfileFormData) {
  return client<Profile>("profiles", "post", {
    json,
  });
}

/**
 * Get a specific profile by ID.
 */
export function getProfile(id: Profile["id"]) {
  return client<Profile>(`profiles/${id}`, "get");
}

/**
 * Get profile notifiication by ID.
 */
export function getProfileNotifications(id: Notifications["id"]) {
  return client<Notifications[]>(`profiles/${id}/notifications`, "get");
}

/**
 * Get profile notifiication by ID.
 */
export function getProfileNotification(
  notificationId: Notifications["id"],
  profileId: Profile["id"],
) {
  return client<Notifications>(
    `profiles/${profileId}/notifications/${notificationId}`,
    "get",
  );
}

/**
 * Update profile.
 */
export function updateProfile(id: Profile["id"], json: ProfileFormData) {
  return client<Profile>(`profiles/${id}`, "put", {
    json,
  });
}

/**
 * Update profile notification.
 */
export function updateNotificationStatus(
  notificationId: Notifications["id"],
  profileId: Profile["id"],
) {
  return client<Notifications>(
    `profiles/${profileId}/notifications/${notificationId}`,
    "put",
  );
}

/**
 * Delete profile.
 */
export function deleteProfile(id: Profile["id"]) {
  return client<Profile>(`profiles/${id}`, "delete");
}
