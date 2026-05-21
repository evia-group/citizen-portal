import { setTimeout } from "node:timers/promises";
import { initRoutes as initApplicationsRoutes } from "@repo/applications/mocks";
import { initRoutes as initMailboxRoutes } from "@repo/mailbox/mocks";
import { initRoutes as initProfileRoutes } from "@repo/profile/mocks";
import { initDogRoutes, initServicesRoutes } from "@repo/services/mocks";
import {
  useBase as base,
  createApp,
  createRouter,
  defineEventHandler,
  handleCors,
} from "h3";

export const app = createApp({
  onBeforeResponse: async () => {
    // delay for 1 second to see loading spinners ;)
    await setTimeout(1000);
  },
});

// CORS
app.use(
  defineEventHandler((event) => {
    if (
      handleCors(event, {
        origin: "*",
        methods: "*",
      })
    ) {
      return;
    }
  }),
);

const router = createRouter();

app.use(base("/api/v1", initProfileRoutes(router).handler));
app.use(base("/api/v1", initServicesRoutes(router).handler));
app.use(base("/api/v1", initDogRoutes(router).handler));
app.use(base("/api/v1", initMailboxRoutes(router).handler));
app.use(base("/api/v1", initApplicationsRoutes(router).handler));
