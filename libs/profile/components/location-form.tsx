import { Button, Debug, LoadingIndicator, Select, Text } from "@repo/ui";
import { useForm } from "react-hook-form";
import { View } from "react-native";
import type { Location } from "../models/location";
import type { Profile } from "../models/profile";

interface LocationFormProps {
  profile: Profile;
  locations: Location[];
  onSubmit: (location: Location) => void;
  onBack: () => void;
}

export function LocationForm({
  profile,
  locations,
  onBack,
  onSubmit,
}: LocationFormProps) {
  const {
    handleSubmit,
    control,
    formState: { isSubmitting, isValid },
    watch,
  } = useForm<{ locationId: string }>({
    mode: "onBlur",
    defaultValues: {
      locationId: `${profile.location?.id}`,
    },
  });

  return (
    <View className="w-full">
      <Debug data={watch()} />

      <View className="mb-8">
        <Select
          control={control}
          name="locationId"
          label="Kommune"
          items={locations.map((loc) => ({
            label: loc.name,
            value: `${loc.id}`,
          }))}
          placeholder="Bitte auswählen"
          rules={{ required: "Bitte wählen Sie eine Kommune!" }}
        />
      </View>

      <View className="flex flex-row justify-between">
        <Button onPress={onBack}>
          <Text>Zurück</Text>
        </Button>
        <Button
          onPress={handleSubmit(({ locationId }) => {
            const location =
              locations.find((loc) => loc.id === +locationId) ?? locations[0];

            onSubmit(location);
          })}
          disabled={isSubmitting || !isValid}
        >
          <Text>Speichern</Text>
        </Button>
      </View>
      {isSubmitting && <LoadingIndicator />}
    </View>
  );
}
