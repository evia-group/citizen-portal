import {
  type Router,
  defineEventHandler,
  getQuery,
  getRouterParam,
  readBody,
} from "h3";
import type { CreateMail, Mail } from "../models/mail";

export function initRoutes(router: Router) {
  const fakeDb = [
    {
      id: 1,
      subject: "Test Subject",
      text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n\nSed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
      status: "PENDING",
      sendAt: "2024-05-06T08:46:21.654867Z",
      sender: "service1",
      receiver: "profileName1",
      profileId: 1,
      applicationId: 1,
    },
    {
      id: 2,
      subject: "Test Subject 2",
      text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n\nSed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
      status: "VIEWED",
      sendAt: "2024-05-05T08:46:21.654867Z",
      sender: "service1",
      receiver: "profileName1",
      profileId: 1,
      applicationId: 2,
    },
    {
      id: 3,
      subject: "Test Subject 3",
      text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n\nSed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
      status: "VIEWED",
      sendAt: "2024-05-05T08:46:21.654867Z",
      sender: "service1",
      receiver: "profileName1",
      profileId: 1,
      applicationId: 2,
    },
  ] satisfies Mail[];

  router.get(
    "/mailbox-messages",
    defineEventHandler((e) => {
      const queryParam = getQuery<{ applicationId?: string }>(e);

      if (queryParam.applicationId) {
        return fakeDb.filter(
          (mail) => mail.applicationId === Number(queryParam.applicationId),
        );
      }
      return fakeDb;
    }),
  );

  router.patch(
    "/mailbox-messages/:id",
    defineEventHandler(async (event) => {
      const id = Number(getRouterParam(event, "id") ?? 1);
      const body = await readBody<Pick<Mail, "status">>(event);

      const fakeMail = fakeDb.find((mail) => mail.id === id);

      if (!fakeMail) {
        throw new Error("Mail not found");
      }

      fakeMail.status = body.status;

      return fakeMail;
    }),
  );

  router.post(
    "/mailbox-messages",
    defineEventHandler(async (event) => {
      const body = await readBody<CreateMail>(event);

      const newMail = {
        ...body,
        id: fakeDb.length + 1,
        sendAt: new Date().toISOString(),
      } satisfies Mail;

      fakeDb.push(newMail);

      return newMail;
    }),
  );

  return router;
}
