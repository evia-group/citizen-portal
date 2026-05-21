export type Mail = {
  id: number;
  subject: string;
  text: string;
  status: "VIEWED" | "PENDING";
  sendAt: string;
  sender: string;
  receiver: string;
  profileId: number;
  applicationId: number;
};

export type CreateMail = Omit<Mail, "id" | "sendAt">;
