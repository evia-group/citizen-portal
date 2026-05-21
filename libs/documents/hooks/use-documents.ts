import {
  type UndefinedInitialDataOptions,
  type UseMutationOptions,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import type { CreateDocument, Document } from "../models/document";
import { createDocument, getDocumentsByProfileId } from "../services/documents";

export const useDocuments = (
  profileId: number,
  documentName?: string,
  options?: Omit<
    UndefinedInitialDataOptions<Document[]>,
    "queryKey" | "queryFn"
  >,
) => {
  return useQuery<Document[]>({
    queryKey: ["documents", profileId],
    queryFn: () =>
      getDocumentsByProfileId(profileId, documentName ? { documentName } : {}),
    ...options,
  });
};

// Mutation hook to create a document
export const useCreateDocument = (
  profileId: number,
  options?: Omit<
    UseMutationOptions<Document, Error, CreateDocument>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (document: CreateDocument) =>
      createDocument(profileId, document),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["documents", profileId] });
    },
    ...options,
  });
};
