import {
  Button,
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  H2,
  Input,
  LoadingIndicator,
  Select,
  Text,
} from "@repo/ui";
import { Plus } from "lucide-react-native";
import { useFieldArray, useForm } from "react-hook-form";
import { View } from "react-native";
import type { Profile } from "../main";
import type { ProfileFormData } from "../models/profile";

interface ProfileFormProps {
  profile: Profile;
  onSubmit: (profile: ProfileFormData) => Promise<void> | void;
  onBack: () => void;
}

export function ProfileForm({ profile, onBack, onSubmit }: ProfileFormProps) {
  const {
    handleSubmit,
    control,
    formState: { isDirty, isSubmitting },
    reset,
  } = useForm<ProfileFormData>({
    mode: "onBlur",
    defaultValues: profile,
  });
  const { fields, append } = useFieldArray({
    control,
    name: "relationships",
  });

  return (
    <View className="w-full">
      {/* <Text>{JSON.stringify(watch(), undefined, 2)}</Text> */}
      <H2 className="mb-3">Persönliche Daten</H2>
      <View className="flex flex-row gap-3 mb-3">
        <Select
          control={control}
          name="salutation"
          label="Anrede"
          items={[
            { label: "Frau", value: "FEMALE" },
            { label: "Herr", value: "MALE" },
            { label: "Keine Angabe", value: "OTHER" },
          ]}
          placeholder="Bitte auswählen"
          readOnly
        />
        <Select
          control={control}
          name="grade"
          label="Doktorgrad"
          items={[
            { label: "Dr.", value: "DR" },
            { label: "Dr. Hc.", value: "DR_HC" },
            { label: "Dr. Eh.", value: "DR_EH" },
          ]}
          placeholder="Bitte auswählen"
          readOnly
        />
      </View>

      <View className="flex flex-row gap-3 mb-3 items-end">
        <Input
          control={control}
          name="firstName"
          label="Vorname/n *"
          className="w-1/2 shrink"
          rules={{ required: "Bitte Vorname ausfüllen!" }}
          readOnly
        />
        <Input
          control={control}
          name="lastName"
          label="Nachname *"
          className="w-1/2 shrink"
          readOnly
        />
      </View>

      <View className="flex flex-row gap-3 mb-3 items-end">
        <Input
          control={control}
          name="birthName"
          label="Geburtsname (falls abweichend)"
          className="w-1/2 shrink"
          readOnly
        />
        <Input
          control={control}
          name="birthDate"
          label="Geburtsdatum *"
          className="w-1/2 shrink"
          readOnly
        />
      </View>

      <View className="flex flex-row gap-3 mb-8">
        <Input
          control={control}
          name="birthLocation"
          label="Geburtsort *"
          className="w-1/2 shrink"
          readOnly
        />
      </View>

      <H2 className="mb-3">Adresse</H2>

      <View className="flex flex-row gap-3 mb-3 items-end">
        <Input
          control={control}
          name="address.street"
          label="Straße & Hausnr. *"
          className="w-1/2 shrink"
          readOnly
        />
        <Input
          control={control}
          name="address.zipCode"
          label="Postleitzahl *"
          className="w-1/2 shrink"
          readOnly
        />
      </View>

      <View className="flex flex-row gap-3 mb-8 items-end">
        <Input
          control={control}
          name="address.city"
          label="Ort *"
          className="w-1/2 shrink"
          readOnly
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
          readOnly
        />
      </View>

      <H2 className="mb-3">Kontaktdaten</H2>

      <View className="flex flex-row gap-3 mb-3 items-end">
        {/* <Select
          label="Ländervorwahl"
          items={[
            { label: "Deutschland +49", value: "+49" },
            { label: "Österreich +43", value: "+43" },
            { label: "Schweiz +41", value: "+41" },
          ]}
          placeholder="Bitte auswählen"
          className="w-1/2 shrink"
        /> */}
        <Input
          control={control}
          name="contactData.phone"
          label="Telefonnummer"
          className="w-1/2 shrink"
          readOnly
        />
      </View>

      <View className="flex flex-row gap-3 mb-8 items-end">
        <Input
          control={control}
          name="contactData.email"
          label="E-Mail Adresse *"
          type="email"
          className="w-1/2 shrink"
          readOnly
        />
        <Input
          control={control}
          name="contactData.demail"
          label="De-Mail Adresse"
          type="email"
          className="w-1/2 shrink"
          readOnly
        />
      </View>

      <H2 className="mb-3">Meine Gemeinschaft</H2>
      <Dialog>
        <View className="flex flex-col items-start">
          <DialogTrigger className="mb-8 border-2 border-teal-700 rounded-full p-2">
            <Plus size={28} color="teal" />
          </DialogTrigger>
        </View>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Typ</DialogTitle>
          </DialogHeader>
          <DialogClose
            onPress={() => {
              append(
                {
                  type: "MOTHER",
                  name: "",
                  birthDate: "",
                },
                { shouldFocus: false },
              );
            }}
            asChild
          >
            <Button>
              <Text>Mensch</Text>
            </Button>
          </DialogClose>
          <DialogClose
            onPress={() => {
              append(
                {
                  type: "DOG",
                  name: "",
                  birthDate: undefined,
                },
                { shouldFocus: false },
              );
            }}
            asChild
          >
            <Button>
              <Text>Tier</Text>
            </Button>
          </DialogClose>
        </DialogContent>
      </Dialog>

      {fields.map((entity, index) => (
        <View key={entity.id}>
          {!["DOG", "CAT"].includes(entity.type) ? (
            <View className="flex flex-row gap-3 mb-3 items-end">
              <Select
                control={control}
                name={`relationships.${index}.type`}
                label="Beziehung"
                items={[
                  { label: "Mutter", value: "MOTHER" },
                  { label: "Vater", value: "FATHER" },
                  { label: "Kind", value: "KID" },
                  { label: "Ehefrau", value: "WIFE" },
                  { label: "Ehemann", value: "HUSBAND" },
                ]}
                placeholder="Bitte auswählen"
                className="w-1/3 shrink"
                rules={{ required: "Bitte ausfüllen!" }}
              />
              <Input
                control={control}
                name={`relationships.${index}.name`}
                label="Vorname/n *"
                className="w-1/3 shrink"
                rules={{ required: "Bitte ausfüllen!" }}
              />
              <Input
                control={control}
                name={`relationships.${index}.birthDate`}
                label="Geburtsdatum *"
                className="w-1/3 shrink"
                rules={{ required: "Bitte ausfüllen!" }}
              />
            </View>
          ) : (
            <View className="flex flex-row gap-3 mb-3 items-end">
              <Select
                control={control}
                name={`relationships.${index}.type`}
                label="Tierart"
                items={[
                  { label: "Hund", value: "DOG" },
                  { label: "Katze", value: "CAT" },
                ]}
                placeholder="Bitte auswählen"
                className="w-1/2 shrink"
                rules={{ required: "Bitte ausfüllen!" }}
              />
              <Input
                control={control}
                name={`relationships.${index}.name`}
                label="Name *"
                className="w-1/2 shrink"
                rules={{ required: "Bitte ausfüllen!" }}
              />
            </View>
          )}
        </View>
      ))}

      <View className="flex flex-row justify-between">
        <Button onPress={onBack}>
          <Text>Zurück</Text>
        </Button>
        <Button
          onPress={handleSubmit(async (values) => {
            await onSubmit(values);
            reset(values);
          })}
          disabled={isSubmitting || !isDirty}
        >
          <Text>Speichern</Text>
        </Button>
      </View>
      {isSubmitting && <LoadingIndicator />}
    </View>
  );
}
