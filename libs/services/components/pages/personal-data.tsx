import {
  type Profile,
  type ProfileFormData,
  useUpdateProfile,
} from "@repo/profile";
import { Button, H2, Input, LoadingIndicator, Select, Text } from "@repo/ui";
import { router } from "expo-router";
import { useForm } from "react-hook-form";
import { View } from "react-native";
import type { Service } from "../../models/services";
import { CancelDialog } from "../cancel-dialog";
import { ProgressSteps } from "../progress";

interface Props {
  profile: Profile;
  service: Service;
  addService: (service: Service) => void;
  addProfile: (profile: Profile) => void;
}

export function PersonalData({
  profile,
  service,
  addProfile,
  addService,
}: Props) {
  const mutation = useUpdateProfile(profile.id);
  const {
    handleSubmit,
    control,
    formState: { isSubmitting },
  } = useForm<ProfileFormData>({
    mode: "onBlur",
    defaultValues: profile,
  });

  return (
    <>
      <ProgressSteps count={4} step={1} title="Persönliche Angaben" />
      <View>
        <H2 className="mb-3">Ihre persönlichen Angaben</H2>
        <View className="flex flex-row gap-3 mb-3 items-end">
          <Input
            control={control}
            name="firstName"
            label="Vorname/n *"
            className="w-1/2 shrink"
            readOnly={true}
            rules={{ required: "Bitte Vorname ausfüllen!" }}
          />
          <Input
            control={control}
            name="lastName"
            label="Nachname *"
            className="w-1/2 shrink"
            readOnly={true}
            rules={{ required: "Bitte Vorname ausfüllen!" }}
          />
        </View>

        <H2 className="mb-3">Wohnanschrift</H2>
        <View className="flex flex-row gap-3 mb-3 items-end">
          <Input
            control={control}
            name="address.street"
            label="Straße & Hausnr. *"
            className="w-1/2 shrink"
            readOnly={true}
          />
          <Input
            control={control}
            name="address.zipCode"
            label="Postleitzahl *"
            className="w-1/2 shrink"
            readOnly={true}
          />
        </View>

        <View className="flex flex-row gap-3 mb-8 items-end">
          <Input
            control={control}
            name="address.city"
            label="Ort *"
            className="w-1/2 shrink"
            readOnly={true}
          />
          <Select
            control={control}
            name="address.country"
            label="Land"
            items={[
              { label: "Deutschland", value: "GERMANY" },
              { label: "Österreich", value: "AUSTRIA" },
              { label: "Schweiz", value: "SWITZERLAND" },
            ]}
            placeholder="Bitte auswählen"
            className="w-1/2 shrink"
            readOnly={true}
          />
        </View>

        <H2 className="mb-3">Kontaktdaten</H2>

        <View className="flex flex-row gap-3 mb-3 items-end">
          <Input
            control={control}
            name="contactData.phone"
            label="Telefonnummer"
            className="w-1/2 shrink"
            readOnly={true}
          />
        </View>

        <View className="flex flex-row gap-3 mb-3 items-end">
          <Input
            control={control}
            name="bookingReference"
            label="Buchungszeichen / Kennziffer Steuerbescheid"
            className="w-1/2 shrink"
          />
        </View>

        {/* <View className="flex flex-row gap-3 mb-3 items-end">
            <Input
              control={control}
              name="contactData.phone"
              label="Buchungszeichen / Kennziffer Steuerbescheid"
              className="w-full"
            />
          </View> */}

        <View className="flex flex-row justify-between mt-6">
          <CancelDialog />
          <Button
            onPress={handleSubmit(async (value) => {
              const profileFromServer = await mutation.mutateAsync(value);
              addProfile(profileFromServer);
              addService(service);

              router.navigate("./2");
            })}
            disabled={isSubmitting}
          >
            <Text>Weiter</Text>
          </Button>
        </View>
        {isSubmitting && <LoadingIndicator />}
      </View>
    </>
  );
}
