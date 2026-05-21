import { Button, Input, LoadingIndicator, Text } from "@repo/ui";
import { useForm } from "react-hook-form";
import { View } from "react-native";
import type { Payment } from "../models/payments";
import type { Profile } from "../models/profile";

interface PaymentsFormProps {
  profile: Profile;
  onSubmit: (payment: Payment) => void;
  onBack: () => void;
}

export function PaymentsForm({ profile, onBack, onSubmit }: PaymentsFormProps) {
  const {
    handleSubmit,
    control,
    formState: { isDirty, isSubmitting, isValid },
  } = useForm<Payment>({
    mode: "onBlur",
    defaultValues: profile.paymentData,
  });

  return (
    <View className="w-full">
      {/* <Text>{JSON.stringify(watch(), undefined, 2)}</Text> */}
      <Text className="font-bold mb-3">Bankkonto hinzufügen</Text>

      <View className="flex flex-row gap-3 mb-3 items-start">
        <Input
          control={control}
          name="accountOwner"
          label="Kontoinhaber *"
          className="w-1/2 shrink"
          rules={{ required: "Bitte Kontoinhaber ausfüllen!" }}
        />
        <Input
          control={control}
          name="iban"
          label="IBAN *"
          className="w-1/2 shrink"
          rules={{ required: "Bitte IBAN ausfüllen!" }}
        />
      </View>

      <View className="flex flex-row gap-3 mb-3">
        <Input
          control={control}
          name="bic"
          label="BIC (Swift Code) *"
          className="w-1/2 shrink"
          rules={{ required: "Bitte BIC ausfüllen!" }}
        />
      </View>

      <Text className="font-bold mb-3">SteuerID hinzufügen</Text>

      <View className="flex flex-row gap-3 mb-8">
        <Input
          control={control}
          name="taxId"
          label="SteuerID *"
          className="w-1/2 shrink"
          rules={{ required: "Bitte SteuerID angeben!" }}
        />
      </View>

      <View className="flex flex-row justify-between">
        <Button onPress={onBack}>
          <Text>Zurück</Text>
        </Button>
        <Button
          onPress={handleSubmit(onSubmit)}
          disabled={isSubmitting || !isDirty || !isValid}
        >
          <Text>Speichern</Text>
        </Button>
      </View>
      {isSubmitting && <LoadingIndicator />}
    </View>
  );
}
