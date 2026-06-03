import { defineEventHandler, getRouterParam, type Router, readBody } from "h3";
import type { Profile, ProfileList } from "../models/profile";
import { locations } from "./location.mocks";

export function initRoutes(router: Router) {
  let idCounter = 1;

  const profile = {
    id: idCounter,
    salutation: "MALE",
    birthDate: "1990-01-01",
    location: locations[0],
    address: {
      city: "New York",
      street: "123 Main St",
      houseNumber: 123,
      zipCode: 10001,
      country: "GERMANY",
    },
    birthLocation: "USA",
    contactData: {
      email: "something@domain.de",
    },
    bookingReference: "",
    firstName: "John",
    lastName: "Doe",
    relationships: [
      {
        id: 1,
        name: "Jane Doe",
        type: "WIFE",
        birthDate: "1990-01-01",
      },
      {
        id: 2,
        name: "Fifi",
        type: "DOG",
        birthDate: "2020-01-01",
      },
    ],
    paymentData: {
      accountOwner: "John Doe",
      iban: "DE89370400440532013000",
      bic: "COBADEFFXXX",
      taxId: "DE123456789",
    },
  } satisfies Profile;

  router.get(
    "/me",
    defineEventHandler(() => profile),
  );

  router.get(
    "/profiles",
    defineEventHandler(() => {
      return [profile] satisfies ProfileList;
    }),
  );

  router.post(
    "/profiles",
    defineEventHandler(async (event) => {
      const body = await readBody<Omit<Profile, "id">>(event);
      return {
        ...body,
        id: idCounter++,
      };
    }),
  );

  router.get(
    "/profiles/:id",
    defineEventHandler((event) => {
      const id = getRouterParam(event, "id") ?? profile.id;
      return {
        ...profile,
        id,
      };
    }),
  );

  router.put(
    "/profiles/:id",
    defineEventHandler(async (event) => {
      const id = getRouterParam(event, "id") ?? profile.id;
      const body = await readBody<Profile>(event);
      return {
        ...profile,
        ...body,
        id,
      };
    }),
  );

  router.delete(
    "/profiles/:id",
    defineEventHandler((event) => {
      const id = getRouterParam(event, "id") ?? profile.id;
      return {
        ...profile,
        id,
      };
    }),
  );

  return router;
}
