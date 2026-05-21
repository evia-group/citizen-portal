import type { Profile, Relationship } from "@repo/profile";
import type { Service } from "./services";

export type Dog = {
  id: number;
  name: string;
  taxStampNumber: string;
  race: string;
  bookingReference: string;
  relationship: Omit<Relationship, "type"> & { type: "DOG" };
};

export type DogFormData = Omit<Dog, "id">;

export type Application = {
  id: number;
  /**
   * Status of the application
   * - `ADDED`: Application has been added by the user
   * - `STARTED`: Application is in progress by a service user
   * - `PENDING`: Application is pending for further information (service user needs info from the user)
   * - `FINISHED`: Application has been finished
   * - `ARCHIVED`: Application has been archived
   * - `CANCELED`: Application has been canceled
   */
  status:
    | "ADDED"
    | "STARTED"
    | "PENDING"
    | "FINISHED"
    | "ARCHIVED"
    | "CANCELED";
  statusValue: string;
  profile: Profile;
  service: Service;
  createdDate: string;
  updatedDate: string;
};

export const statusMap = {
  ADDED: "offen",
  STARTED: "In Bearbeitung",
  PENDING: "Rückfrage vorhanden",
  FINISHED: "abgeschlossen",
  ARCHIVED: "archiviert",
  CANCELED: "storniert",
} as const satisfies Record<Application["status"], string>;

export type ApplicationFormData = Omit<
  Application,
  "id" | "status" | "createdDate" | "updatedDate" | "statusValue"
>;

export type DogApplication = {
  id?: number;
  application: Application;
  dog: Dog;
  justification: "LOST_STAMP" | "STAMP_UNUSABLE";
};

export type DogApplicationFormData = {
  application: ApplicationFormData;
  dog: DogFormData;
  justification: DogApplication["justification"];
};

/** Current state of the data during the different steps of the form */
export type DogApplicationState = {
  profile?: Profile;
  service?: Service;
  dog?: Dog;
  justification?: DogApplication["justification"];
};
