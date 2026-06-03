"use client";

import { useApplication, useUpdateApplication } from "@repo/applications";
import { type CreateMail, useMailbox, useSendMail } from "@repo/mailbox";
import {
  Button,
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
  Debug,
  Dialog,
  DialogContent,
  H1,
  Input,
  Text,
  Textarea,
  useDisclosure,
} from "@repo/ui";
import { CircleCheck } from "lucide-react";
import { useForm } from "react-hook-form";

export default function MailboxPage({
  params: { applicationId },
}: {
  params: { applicationId: string };
}) {
  const { data: mails } = useMailbox(1, new URLSearchParams({ applicationId }));
  const { control, handleSubmit, watch, reset } = useForm<CreateMail>({
    defaultValues: {
      subject: "",
      text: "",
      profileId: 1,
      receiver: "benutzer",
      sender: "service-user",
      applicationId: Number(applicationId),
      status: "PENDING",
    },
  });
  const sendMail = useSendMail();
  const [modalVisible, { open, close }] = useDisclosure(false);
  const updateApplication = useUpdateApplication();
  const { data: application } = useApplication(+applicationId);

  return (
    <div>
      <H1 className="mb-7">Mailbox</H1>

      <ul className="flex flex-col gap-2">
        {mails?.map((mail) => (
          <Collapsible key={mail.id} asChild>
            <li className="border-b border-border">
              <CollapsibleTrigger>
                <Text>{mail.subject}</Text>
              </CollapsibleTrigger>
              <CollapsibleContent>
                <Text>{mail.text}</Text>
              </CollapsibleContent>
            </li>
          </Collapsible>
        ))}
      </ul>

      <div className="mt-8">
        <Dialog
          open={modalVisible}
          onOpenChange={(value) => (value ? open() : close())}
        >
          <DialogContent>
            <div className="flex flex-row gap-4 items-center justify-center">
              <CircleCheck size={28} color="green" />
              <Text>Nachricht wurden erfolgreich gesendet!</Text>
            </div>
          </DialogContent>
        </Dialog>
        <Input control={control} name="subject" label="Subject" type="text" />
        <Textarea
          control={control}
          name="text"
          label="Text *"
          type="text"
          rules={{ required: "Bitte ausfüllen!" }}
        />
        <Debug data={watch()} />
        {application ? (
          <Button
            onPress={handleSubmit(async (values) => {
              await sendMail.mutateAsync(values);
              await updateApplication.mutateAsync({
                ...application,
                status: "PENDING",
              });
              reset();

              open();
              setTimeout(() => {
                close();
              }, 2_000);
            })}
            className="mt-4"
          >
            <Text>Senden</Text>
          </Button>
        ) : null}
      </div>
    </div>
  );
}
