export type Document = {
  id: number;
  profileId: number;
  name: string;
  type: "IDENTITY_CARD" | "REGISTRATION_FORM" | "OTHER";
  isArchive: boolean;
  fileId?: string;
};

export type CreateDocument = Omit<Document, "id">;
