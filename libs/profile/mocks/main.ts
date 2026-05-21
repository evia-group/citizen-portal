import type { Router } from "h3";
import { initRoutes as initLocationMocks } from "./location.mocks";
import { initRoutes as initProfileMocks } from "./profile.mocks";

export function initRoutes(router: Router) {
  initProfileMocks(router);
  initLocationMocks(router);
  return router;
}

export { locations } from "./location.mocks";
