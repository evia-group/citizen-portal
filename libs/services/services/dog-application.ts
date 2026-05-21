import { client } from "@repo/shared";
import type {
  DogApplication,
  DogApplicationFormData,
} from "../models/dog-application";

/**
 * Create a new dog application.
 */
export function createDogApplication(json: DogApplicationFormData) {
  return client<DogApplication>("dogs-applications", "post", {
    json,
  });
}

/**
 * Get a specific dog application by ID.
 */
export function getDogApplication(id: DogApplication["id"]) {
  return client<DogApplication>(`dogs-applications/${id}`, "get");
}

/**
 * Get a list of dog applications
 */
export function getDogApplications() {
  return client<DogApplication[]>("dogs-applications", "get");
}
