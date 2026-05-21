import {
  type UndefinedInitialDataOptions,
  type UseMutationOptions,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import type { CreateMail, Mail } from "../models/mail";
import {
  getMailsByProfileId,
  markMailAsRead,
  sendMail,
} from "../services/mailbox";

export const useMailbox = (
  profileId: number,
  searchParams?: URLSearchParams,
  options?: Omit<UndefinedInitialDataOptions<Mail[]>, "queryKey" | "queryFn">,
) => {
  return useQuery<Mail[]>({
    queryKey: ["mailbox", profileId, searchParams],
    queryFn: () => getMailsByProfileId(profileId, searchParams),
    ...options,
  });
};

/**
 * Mutation hook to mark a mail as read.
 */
export const useMarkMailAsRead = (
  options?: Omit<
    UseMutationOptions<Mail, Error, Mail["id"]>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: markMailAsRead,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["mailbox"] });
    },
    ...options,
  });
};

/**
 * Mutation hook to send a mail.
 */
export const useSendMail = (
  options?: Omit<
    UseMutationOptions<Mail, Error, CreateMail>,
    "mutationFn" | "onSuccess"
  >,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: sendMail,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["mailbox"] });
    },
    ...options,
  });
};
