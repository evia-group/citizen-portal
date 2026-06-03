import type { Profile } from "@repo/profile";
import type { Service } from "@repo/services";
import {
  defineEventHandler,
  getRouterParam,
  type Router,
  setResponseStatus,
} from "h3";
import type { Application } from "../models/application";

export function initRoutes(router: Router) {
  const profile = {
    id: 1,
    salutation: "MALE",
    birthDate: "1990-01-01",
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

  const fakeDb = [
    {
      id: 1,
      status: "PENDING",
      createdDate: "2024-05-06T08:46:21.654867Z",
      profile,
      service,
      statusValue: "PENDING",
      updatedDate: "2024-05-06T08:46:21.654867Z",
    },
    {
      id: 2,
      status: "FINISHED",
      createdDate: "2024-05-06T08:46:21.654867Z",
      profile,
      service,
      statusValue: "FINISHED",
      updatedDate: "2024-05-06T08:46:21.654867Z",
    },
    {
      id: 3,
      status: "PENDING",
      createdDate: "2024-05-06T08:46:21.654867Z",
      profile,
      service,
      statusValue: "PENDING",
      updatedDate: "2024-05-06T08:46:21.654867Z",
    },
  ] satisfies Application[];

  router.get(
    "/applications",
    defineEventHandler(() => {
      return fakeDb;
    }),
  );

  router.get(
    "/applications/:id",
    defineEventHandler((event) => {
      const id = Number(getRouterParam(event, "id") ?? profile.id);
      const application = fakeDb.find((app) => app.id === id);
      if (!application) {
        setResponseStatus(event, 404, "Not Found");
        return "Not Found";
      }
      return application;
    }),
  );

  return router;
}
