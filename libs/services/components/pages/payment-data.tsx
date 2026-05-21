import {
  Button,
  Checkbox,
  H2,
  H3,
  LoadingIndicator,
  RadioGroup,
  RadioGroupItem,
  Text,
} from "@repo/ui";
import { Link, router } from "expo-router";
import { useForm } from "react-hook-form";
import { View } from "react-native";
import { useCreateDogApplication } from "../../hooks/use-dog-application";
import type { DogApplicationState } from "../../models/dog-application";
import type { Service } from "../../models/services";
import { CancelDialog } from "../cancel-dialog";
import { ProgressSteps } from "../progress";

export type NonOptional<Type> = {
  [Key in keyof Type]-?: Type[Key];
};

interface Props {
  service: Service;
  dogApplicationState: NonOptional<DogApplicationState>;
}

export function PaymentData({ service, dogApplicationState }: Props) {
  const { control, handleSubmit, formState } = useForm<{
    paymentMethod?: "PAYPAL";
    consent: boolean;
  }>({
    mode: "onBlur",
    defaultValues: {
      paymentMethod: undefined,
      consent: false,
    },
  });

  const mutation = useCreateDogApplication();

  return (
    <>
      <ProgressSteps count={4} step={4} title="Kosten" />
      <View>
        <H2 className="mb-3">Kosten</H2>

        <Text className="font-bold mb-1">
          Rechnung zum Service "Ersatzmarke beantragen"
        </Text>
        <Text className="mb-1">Rechnungsbetrag: {service.cost} €</Text>
        <Text className="mb-1">Fälligkeitsdatum: 22.05.2024</Text>

        <H3 className="mt-6 mb-3">Zahlungsmethoden</H3>
        <View className="flex flex-row justify-between">
          <RadioGroup
            name="paymentMethod"
            control={control}
            rules={{ required: "Bitte Zahlungsmethode auswählen!" }}
          >
            <RadioGroupItem value="PAYPAL">PayPal</RadioGroupItem>
          </RadioGroup>
        </View>

        <H3 className="mt-6 mb-3">Zustimmung</H3>
        <View className="flex flex-row justify-between">
          <Checkbox
            name="consent"
            control={control}
            rules={{ required: "Bitte Zustimmung erteilen!" }}
          >
            Ich stimme zu, dass meine persönlichen Daten für die Nutzung gemäß
            den Datenschutzrichtlinien verarbeitet werden dürfen. *
          </Checkbox>
        </View>

        <View className="flex flex-row gap-2 justify-between mt-6">
          <View className="flex flex-col gap-2">
            <Link href="./3" asChild>
              <Button variant="secondary">
                <Text>Zurück</Text>
              </Button>
            </Link>
            <CancelDialog />
          </View>
          <Button
            disabled={mutation.isPending || !formState.isValid}
            onPress={handleSubmit(async () => {
              await mutation.mutateAsync({
                dog: dogApplicationState.dog,
                justification: dogApplicationState.justification,
                application: {
                  service: dogApplicationState.service,
                  profile: dogApplicationState.profile,
                },
              });
              router.navigate("./5");
            })}
          >
            <Text>Bezahlen und absenden</Text>
          </Button>
        </View>
        {(mutation.isPending || formState.isSubmitting) && <LoadingIndicator />}
      </View>
    </>
  );
}
