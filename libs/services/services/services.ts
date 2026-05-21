import { type SearchParamsOption, client } from "@repo/shared";
import type {
  Application,
  ApplicationFormData,
} from "../models/dog-application";
import type { Category, Domain, Service } from "../models/services";

/**
 * Get a list of services.
 */
export async function getServicesList(searchParams?: SearchParamsOption) {
  const result = await client<Service[]>("services", "get", {
    searchParams,
  });

  const domains = new Map<string, Domain>();
  const categories = new Map<string, Category>();
  const services = new Map<string, Service>();

  for (const service of result) {
    domains.set(service.category.domainSlug, {
      icon: service.category.domainIcon,
      slug: service.category.domainSlug,
      title: service.category.domainName,
      categories: [],
    });

    categories.set(service.category.slug, {
      id: service.category.id,
      icon: service.category.icon,
      slug: service.category.slug,
      title: service.category.name,
      services: [],
    });

    services.set(service.slug, service);
  }

  for (const item of result) {
    const category = categories.get(item.category.slug);
    const service = services.get(item.slug);

    if (category && service && !category.services.includes(service)) {
      category.services.push(service);
    }
  }

  for (const item of result) {
    const domain = domains.get(item.category.domainSlug);
    const category = categories.get(item.category.slug);

    if (domain && category && !domain.categories.includes(category)) {
      domain.categories.push(category);
    }
  }

  return Array.from(domains.values());
}

export async function createApplication(application: ApplicationFormData) {
  return client<Application>("applications", "post", {
    json: application,
  });
}
