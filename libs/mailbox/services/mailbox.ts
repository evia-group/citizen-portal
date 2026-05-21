import { client } from "@repo/shared";
import type { CreateMail, Mail } from "../models/mail";

export function getMailsByProfileId(
  profileId: number,
  searchParams?: URLSearchParams,
) {
  const params = new URLSearchParams(searchParams);
  params.append("profileId", profileId.toString());

  return client<Mail[]>("mailbox-messages", "get", {
    searchParams: params,
  });
}

export function markMailAsRead(mailId: Mail["id"]) {
  return client<Mail>(`mailbox-messages/${mailId}`, "patch", {
    json: { status: "VIEWED" } satisfies Pick<Mail, "status">,
  });
}

export function sendMail(mail: CreateMail) {
  return client<Mail>("mailbox-messages", "post", {
    json: mail,
  });
}
