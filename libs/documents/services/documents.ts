import { type SearchParamsOption, basePath, client } from "@repo/shared";
import type { CreateDocument, Document } from "../models/document";

export function getDocumentsByProfileId(
  profileId: number,
  searchParams?: SearchParamsOption,
) {
  return client<Document[]>(`profiles/${profileId}/documents`, "get", {
    searchParams,
  });
}

export function getDocumentDownloadLink(profileId: number, documentId: number) {
  return `${basePath}/profiles/${profileId}/documents/${documentId}/download`;
}

export function createDocument(profileId: number, document: CreateDocument) {
  return client<Document>(`/profiles/${profileId}/documents`, "post", {
    json: document,
  });
}
