import {
  type Profile,
  type Relationship,
  useUpdateProfile,
} from "@repo/profile";
import {
  Button,
  H2,
  Input,
  LoadingIndicator,
  RadioGroup,
  RadioGroupItem,
  Select,
  Text,
} from "@repo/ui";
import { Link, router } from "expo-router";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { View } from "react-native";
import type { Dog, DogApplication } from "../../models/dog-application";
import { CancelDialog } from "../cancel-dialog";
import { ProgressSteps } from "../progress";

type Props = {
  addDog: (dog: Dog) => void;
  addJustification: (justification: DogApplication["justification"]) => void;
  profile: Profile;
  addProfile: (profile: Profile) => void;
};

export function DogData({
  addDog,
  addJustification,
  addProfile,
  profile,
}: Props) {
  const {
    handleSubmit,
    setValue,
    control,
    watch,
    formState: { isSubmitting },
  } = useForm<
    Pick<DogApplication, "dog" | "justification"> & { relationshipId: string }
  >({
    mode: "onBlur",
    defaultValues: {
      dog: {
        taxStampNumber: "",
        name: "",
        race: "",
        bookingReference: "",
      },
      justification: undefined,
      relationshipId: "",
    },
  });

  const mutation = useUpdateProfile(1);

  async function onSubmit(
    value: Pick<DogApplication, "dog" | "justification"> & {
      relationshipId: string;
    },
  ) {
    // use existing dog relationship
    if (value.relationshipId !== "") {
      const relationship = profile.relationships.find(
        (r) => r.id === +value.relationshipId,
      );
      if (relationship) {
        value.dog.relationship = relationship as Omit<Relationship, "type"> & {
          type: "DOG";
        };
      }
      // create new dog relationship in profile and use it
    } else {
      const updatedProfile = await mutation.mutateAsync({
        ...profile,
        relationships: [
          ...(profile.relationships ?? []),
          {
            name: value.dog.name,
            type: "DOG",
          },
        ],
      });
      value.dog.relationship = updatedProfile.relationships?.find(
        (r) => r.name === value.dog.name,
      ) as Omit<Relationship, "type"> & { type: "DOG" };
      addProfile(updatedProfile);
    }
    addDog(value.dog);
    addJustification(value.justification);

    router.navigate("./3");
  }

  const relationshipId = watch("relationshipId");

  useEffect(() => {
    if (relationshipId !== "") {
      const dog = profile.relationships?.find((r) => r.id === +relationshipId);
      if (dog) {
        setValue("dog.name", dog.name);
      }
    }
  }, [relationshipId, profile, setValue]);

  return (
    <>
      <ProgressSteps count={4} step={2} title="Angaben zum Hund" />
      <View>
        <H2 className="mb-3">Angaben zum Hund</H2>
        <View className="flex flex-col gap-3 mb-6">
          {profile?.relationships ? (
            <Select
              control={control}
              name="relationshipId"
              label="Wählen Sie Ihren Hund aus"
              items={
                profile.relationships
                  .filter((r) => r.type === "DOG")
                  .map((r) => ({
                    value: `${r.id}`,
                    label: r.name,
                  })) ?? []
              }
              placeholder="Bitte auswählen"
              className="w-1/2 shrink"
            />
          ) : null}
          <Input
            control={control}
            type="numeric"
            name="dog.taxStampNumber"
            label="Nummer der Hundesteuermarke *"
            rules={{ required: "Bitte ausfüllen!" }}
          />
          <Input
            control={control}
            name="dog.name"
            label="Wie heißt ihr Hund? *"
            rules={{ required: "Bitte ausfüllen!" }}
            readOnly={!!relationshipId}
          />
          <Select
            control={control}
            name="dog.race"
            label="Hunderasse *"
            items={[
              { label: "Mischling", value: "MISCHLING" },
              { label: "Andere Rasse", value: "ANDERE_RASSE" },
              {
                label: "American Staffordshire Terrier",
                value: "AMERICAN_STAFFORDSHIRE_TERRIER",
              },
              { label: "Bordeaux Dogge", value: "BORDEAUX_DOGGE" },
              { label: "Bullmastiff", value: "BULLMASTIFF" },
              { label: "Bullterrier", value: "BULLTERRIER" },
              { label: "Dogo Argentino", value: "DOGO_ARGENTINO" },
              { label: "Deutscher Schäferhund", value: "GERMAN_SHEPHERD" },
              { label: "Fila Brasiliero", value: "FILA_BRASILIERO" },
              { label: "Mastiff", value: "MASTIFF" },
              { label: "Mastino Napoletano", value: "MASTINO_NAPOLETANO" },
              { label: "Pit Bull Terrier", value: "PIT_BULL_TERRIER" },
              {
                label: "Staffordshire Terrier",
                value: "STAFFORDSHIRE_BERRIER",
              },
              { label: "Tosa Inu", value: "TOSA_INU" },
            ]}
            placeholder="Bitte auswählen"
            className="w-1/2 shrink"
            rules={{ required: "Bitte ausfüllen!" }}
          />
        </View>

        <H2 className="mb-3">Grund für den Ersatz *</H2>

        <View className="flex flex-row gap-3 mb-3 items-end">
          <RadioGroup
            control={control}
            name="justification"
            rules={{ required: "Bitte den Grund für den Ersatz auswählen" }}
          >
            <RadioGroupItem value="STAMP_UNUSABLE">
              Marke unbrauchbar
            </RadioGroupItem>
            <RadioGroupItem value="LOST_STAMP">Marke verloren</RadioGroupItem>
          </RadioGroup>
        </View>

        <View className="mb-6">
          <Text className="text-sm">
            Bitte beachten Sie, dass die Hundesteuermarke Eigentum der
            ausgebenden Stelle ist. Die unbrauchbare oder wiedergefundene
            Hundesteuermarke müssen Sie umgehend zurückgeben. Die Nichtgabe der
            Hundesteuermarke stellt eine Ordnungswidrigkeit nach der
            Hundesteuersatzung dar und kann mit einem Bußgeld belegt werden.
          </Text>
        </View>

        {/* <Text>{JSON.stringify(watch(), undefined, 2)}</Text> */}
        {/*
        <View className="flex flex-row gap-3 mb-3 items-end">
          <Input
            control={control}
            name="dog.bookingReference"
            label="Buchungszeichen / Kennziffer Steuerbescheid"
            className="w-full"
          />
        </View>
*/}
        <View className="flex flex-row justify-between mt-6">
          <Link href="./1" asChild>
            <Button variant="secondary">
              <Text>Zurück</Text>
            </Button>
          </Link>
          <CancelDialog />
          <Button onPress={handleSubmit(onSubmit)} disabled={isSubmitting}>
            <Text>Weiter</Text>
          </Button>
        </View>
        {isSubmitting && <LoadingIndicator />}
      </View>
    </>
  );
}
