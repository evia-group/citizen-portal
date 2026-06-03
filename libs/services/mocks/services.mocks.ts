import { locations } from "@repo/profile/mocks";
import { defineEventHandler, type Router } from "h3";
import type { Service as ServiceDTO } from "../models/services";

export function initRoutes(router: Router) {
  let idCounter = 1;

  type Item = {
    icon: string;
    slug: string;
    title: string;
  };
  type Domain = Item & {
    categories?: Category[];
  };
  type Category = Item & {
    services?: Service[];
  };
  type Service = Item & {
    cost: number;
  };

  const domains = [
    {
      icon: "users",
      slug: "family",
      title: "Familie & Kind",
    },
    {
      icon: "bike",
      slug: "engagement",
      title: "Engagement & Hobbies und noch gaaaaaaanz viele mehr",
      categories: [
        {
          icon: "hand-helping",
          slug: "engagement",
          title: "Engagement & Beteiligung",
        },
        {
          icon: "fish-symbol",
          slug: "fishing",
          title: "Fischen & Jagen",
        },
        {
          icon: "party-popper",
          slug: "events",
          title: "Veranstaltung durchführen",
        },
        {
          icon: "paw-print",
          slug: "animals",
          title: "Tierhaltung",
          services: [
            {
              icon: "dog",
              slug: "dog-tax",
              title: "Hund zur Hundesteuer anmelden",
              cost: 15,
            },
            {
              icon: "shield",
              slug: "replace-dog-tag",
              title: "Ersatzhundemarke beantragen",
              cost: 5,
            },
          ],
        },
        {
          icon: "sailboat",
          slug: "boats",
          title: "Bootbesitz",
        },
      ],
    },
    {
      icon: "heart-pulse",
      slug: "health",
      title: "Gesundheit",
    },
    {
      icon: "book-open-text",
      slug: "education",
      title: "Bildung",
    },
    {
      icon: "briefcase-business",
      slug: "work",
      title: "Arbeit",
    },
    {
      icon: "scale",
      slug: "law",
      title: "Recht & Ordnung",
    },
  ] satisfies Domain[];

  function convertToBackendStructure(domain: Domain): ServiceDTO[] {
    const {
      icon: domainIcon,
      slug: domainSlug,
      title: domainTitle,
      categories,
    } = domain;

    if (categories) {
      return categories.flatMap((category) => {
        const {
          icon: categoryIcon,
          slug: categorySlug,
          title: categoryTitle,
          services,
        } = category;

        if (services) {
          return services.map((service) => {
            const { icon, slug, title } = service;

            return {
              id: idCounter++,
              name: title,
              icon: icon,
              slug: slug,
              cost: service.cost,
              location: locations[0],
              category: {
                id: idCounter++,
                name: categoryTitle,
                slug: categorySlug,
                icon: categoryIcon,
                domainName: domainTitle,
                domainSlug: domainSlug,
                domainIcon: domainIcon,
              },
            } satisfies ServiceDTO;
          });
        }

        return {
          id: idCounter++,
          name: "Nicht verfügbar",
          icon: "octagon-x",
          slug: `not-available-${idCounter++}`,
          cost: 0,
          location: locations[0],
          category: {
            id: idCounter++,
            name: categoryTitle,
            slug: categorySlug,
            icon: categoryIcon,
            domainName: domainTitle,
            domainSlug: domainSlug,
            domainIcon: domainIcon,
          },
        } satisfies ServiceDTO;
      });
    }

    return [
      {
        id: idCounter++,
        name: "Nicht verfügbar",
        icon: "octagon-x",
        slug: `not-available-${idCounter++}`,
        cost: 0,
        location: locations[0],
        category: {
          id: idCounter++,
          name: "Nicht verfügbar",
          slug: `not-available-${idCounter++}`,
          icon: "octagon-x",
          domainName: domainTitle,
          domainSlug: domainSlug,
          domainIcon: domainIcon,
        },
      } satisfies ServiceDTO,
    ];
  }

  router.get(
    "/services",
    defineEventHandler(() => {
      return domains.flatMap(convertToBackendStructure);
    }),
  );

  return router;
}
