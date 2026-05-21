import { type Router, defineEventHandler } from "h3";
import type { Location } from "../models/location";

export const locations = [
  {
    id: 1,
    name: "Leonberg",
    federalState: "Baden-Württemberg",
  },
  {
    id: 2,
    name: "Stuttgart",
    federalState: "Baden-Württemberg",
  },
] satisfies Location[];

export function initRoutes(router: Router) {
  router.get(
    "/locations",
    defineEventHandler(() => {
      return locations;
    }),
  );

  return router;
}
