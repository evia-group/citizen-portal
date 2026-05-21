import { useLoggedInAuth } from "@repo/auth";
import { type CreateDocument, useCreateDocument } from "@repo/documents";
import { Button, Input, Select, Text } from "@repo/ui";
import { router } from "expo-router";
import { useForm } from "react-hook-form";
import { ScrollView, View } from "react-native";

export default function Create() {
  const { profileId } = useLoggedInAuth();
  const { control, handleSubmit } = useForm<CreateDocument>({
    mode: "onBlur",
    defaultValues: {},
  });

  const createDocument = useCreateDocument(profileId);

  return (
    <ScrollView className="bg-white">
      <View className="w-full p-3">
        <View className="flex flex-row gap-3 mb-3 items-end">
          <Input
            control={control}
            name="name"
            label="Dokumentenname"
            className="w-full shrink"
          />
        </View>

        <View className="flex flex-row gap-3 mb-3 items-end">
          <Select
            control={control}
            name="type"
            label="Dokumentenart"
            items={[
              { label: "IDENTITY CARD", value: "IDENTITY_CARD" },
              { label: "REGISTRATION FORM", value: "REGISTRATION_FORM" },
              { label: "OTHER", value: "OTHER" },
            ]}
          />
        </View>

        <View className="flex flex-row justify-between  py-4 items-center ">
          <Button
            onPress={() => {
              router.navigate("/dms");
            }}
          >
            <Text>Zurück</Text>
          </Button>

          <Button
            disabled={createDocument.isPending}
            onPress={handleSubmit(async (values) => {
              values.isArchive = true;
              await createDocument.mutateAsync(values);

              setTimeout(() => {
                router.navigate("/dms");
              }, 2_000);
            })}
          >
            <Text>Dokument hochladen </Text>
          </Button>
        </View>
      </View>
    </ScrollView>
  );
}
