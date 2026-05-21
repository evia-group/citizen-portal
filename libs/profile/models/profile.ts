import type { Location } from "./location";
import type { Payment } from "./payments";

export type Profile = {
  /** Format: int64 */
  id: number;
  /** @enum {string} */
  salutation?: "MALE" | "FEMALE" | "OTHER";
  /** @enum {string} */
  grade?: "DR" | "DR_HC" | "DR_EH";
  firstName: string;
  lastName: string;
  bookingReference: string;
  birthName?: string;
  /** Format: date */
  birthDate: string;
  birthLocation: string;
  address: {
    street: string;
    houseNumber: number;
    zipCode: number;
    city: string;
    country?: string;
  };
  contactData: {
    phone?: number;
    email: string;
    demail?: string;
  };
  relationships: Relationship[];
  location?: Location;
  paymentData?: Payment;
};
export type ProfileList = Profile[];
export type Relationship = {
  /** Format: int64 */
  id: number;
  /** @enum {string} */
  type: "MOTHER" | "FATHER" | "KID" | "WIFE" | "HUSBAND" | "DOG" | "CAT";
  name: string;
  /** Format: date */
  birthDate?: string;
};

export type ProfileFormData = Omit<Profile, "id" | "relationships"> & {
  relationships: RelationshipFormData[];
};
export type RelationshipFormData = Omit<Relationship, "id">;
