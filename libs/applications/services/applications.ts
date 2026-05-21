import type { DogApplication } from "@repo/services";
import { type SearchParamsOption, client } from "@repo/shared";
import type { Application } from "../models/application";

export async function getApplicationsList(searchParams?: SearchParamsOption) {
  return client<Application[]>("applications", "get", {
    searchParams,
  });
}

export function getDogApplicationByApplicationId(id: number) {
  return client<DogApplication>(`applications/${id}/dog_application`, "get");
}

export function getApplicationById(id: number) {
  return client<Application>(`applications/${id}`, "get");
}

export function updateApplication(data: Application) {
  return client<Application>(`applications/${data.id}`, "put", {
    json: data,
  });
}
