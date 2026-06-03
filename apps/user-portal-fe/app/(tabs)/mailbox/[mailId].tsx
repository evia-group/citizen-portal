import { useLoggedInAuth } from "@repo/auth";
import {
  type CreateMail,
  type Mail,
  useMailbox,
  useMarkMailAsRead,
  useSendMail,
} from "@repo/mailbox";
import {
  Button,
  Debug,
  Dialog,
  DialogContent,
  formatDate,
  Separator,
  Text,
  Textarea,
  useDisclosure,
} from "@repo/ui";
import { router, Stack, useLocalSearchParams } from "expo-router";
import { CircleCheck } from "lucide-react-native";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { ScrollView, View } from "react-native";

export default function MailboxMailId() {
  const { mailId } = useLocalSearchParams();
  const { profileId } = useLoggedInAuth();
  const { data: mailbox } = useMailbox(profileId);
  const markMailAsRead = useMarkMailAsRead();
  const [showReply, setShowReply] = useState(false);

  const mail = mailbox?.find((mail) => mail.id === Number(mailId));

  // biome-ignore lint/correctness/useExhaustiveDependencies: mutation variable is "mutated" too often and triggers the effect in a loop
  useEffect(() => {
    if (mail && mail.status !== "VIEWED") {
      markMailAsRead.mutate(mail.id);
    }
  }, [mail]);

  return (
    <ScrollView className="bg-white p-4">
      <Stack.Screen
        options={{
          title: mail?.subject ?? "Mail",
        }}
      />
      {mail ? (
        <View>
          <View className="flex flex-row justify-between">
            <Text className="font-robotoMedium">von: {mail.sender}</Text>
            <Text className="text-gray-500 text-sm">
              {formatDate(mail.sendAt)}
            </Text>
          </View>
          <Separator className="my-3" />
          <View>
            <Text>{mail.text}</Text>
          </View>
          {!showReply && mail.sender.includes("service") ? (
            <Button
              onPress={() => {
                setShowReply(true);
              }}
              className="mt-7"
            >
              <Text>Antworten</Text>
            </Button>
          ) : null}
          {showReply ? <ReplyForm mail={mail} /> : null}
        </View>
      ) : null}
    </ScrollView>
  );
}

function ReplyForm({ mail }: { mail: Mail }) {
  const { control, handleSubmit, watch } = useForm<CreateMail>({
    defaultValues: {
      subject: `Re: ${mail.subject}`,
      text: "",
      profileId: mail.profileId,
      receiver: mail.sender,
      sender: mail.receiver,
      applicationId: mail.applicationId,
      status: "PENDING",
    },
  });
  const sendMail = useSendMail();
  const [modalVisible, { open, close }] = useDisclosure(false);

  return (
    <View className="mt-10">
      <Dialog
        open={modalVisible}
        onOpenChange={(value) => (value ? open() : close())}
      >
        <DialogContent>
          <View className="flex flex-row gap-4 items-center justify-center">
            <CircleCheck size={28} color="green" />
            <Text>Nachricht wurden erfolgreich gesendet!</Text>
          </View>
        </DialogContent>
      </Dialog>
      <Textarea
        control={control}
        name="text"
        label="Text *"
        type="text"
        rules={{ required: "Bitte ausfüllen!" }}
      />
      <Debug data={watch()} />
      <Button
        onPress={handleSubmit(async (values) => {
          await sendMail.mutateAsync(values);

          open();
          setTimeout(() => {
            close();
            router.navigate("/mailbox/");
          }, 2_000);
        })}
        className="mt-4"
      >
        <Text>Senden</Text>
      </Button>
    </View>
  );
}
