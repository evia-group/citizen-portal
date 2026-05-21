import type { Profile } from "@repo/profile";
import { type Router, defineEventHandler, readBody } from "h3";
import type { DogApplication } from "../models/dog-application";
import type { Service } from "../models/services";

export function initRoutes(router: Router) {
  let idCounter = 1;

  router.post(
    "/dogs-applications",
    defineEventHandler(async (event) => {
      const body = await readBody(event).catch(() => {});

      return {
        id: idCounter++,
        ...body,
      };
    }),
  );

  const profile = {
    id: 1,
    salutation: "MALE",
    birthDate: "1990-01-01",
    address: {
      city: "New York",
      street: "123 Main St",
      houseNumber: 1,
      zipCode: 10001,
      country: "GERMANY",
    },
    birthLocation: "USA",
    contactData: {
      email: "something@domain.de",
    },
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

  const service = {
    id: 1,
    icon: "users",
    slug: "family",
    category: {
      domainIcon: "users",
      domainName: "Familie & Kind",
      domainSlug: "family",
      icon: "users",
      name: "Familie & Kind",
      slug: "family",
      id: 1,
    },
    cost: 10,
    name: "Test Service",
    location: {
      id: 1,
      name: "Stuttgart",
      federalState: "Baden-Württemberg",
    },
  } satisfies Service;

  const dogApplication = {
    id: 1,
    application: {
      profile,
      service,
      createdDate: "2024-05-06T08:46:21.654867Z",
      id: 1,
      status: "PENDING",
      statusValue: "PENDING",
      updatedDate: "2024-05-06T08:46:21.654867Z",
    },
    dog: {
      bookingReference: "123456",
      id: 1,
      name: "Fifi",
      race: "Poodle",
      taxStampNumber: "123456",
      relationship: {
        id: 1,
        name: "John Doe",
        type: "DOG",
        birthDate: "2020-01-01",
      },
    },
    justification: "LOST_STAMP",
  } satisfies DogApplication;

  router.get(
    "/dogs-applications",
    defineEventHandler(async () => {
      return [dogApplication];
    }),
  );

  router.get(
    "/applications/:id/dog_application",
    defineEventHandler(async () => {
      return dogApplication;
    }),
  );

  return router;
}
