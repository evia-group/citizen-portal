import type { Location } from "@repo/profile";

export type Service = {
  /** Format: int64 */
  id?: number;
  name: string;
  icon: string;
  slug: string;
  location?: Location;
  category: CategoryDTO;
  cost: number;
};
export type { Location };
export type CategoryDTO = {
  /** Format: int64 */
  id?: number;
  name: string;
  icon: string;
  slug: string;
  domainName: string;
  domainIcon: string;
  domainSlug: string;
};

export type Domain = {
  icon: string;
  slug: string;
  title: string;
  categories: Category[];
};

export type Category = {
  id: CategoryDTO["id"];
  icon: string;
  slug: string;
  title: string;
  services: Service[];
};
