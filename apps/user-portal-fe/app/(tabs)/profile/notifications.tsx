import { Button, Card, Checkbox, H1, Text } from "@repo/ui";
import { router } from "expo-router";
import { User } from "lucide-react-native";
import { useForm } from "react-hook-form";
import { ScrollView, View } from "react-native";

export default function ProfileNotificationsScreen() {
  const { handleSubmit, control } = useForm<{
    pushConsent: boolean;
    emailConsent: boolean;
  }>({
    mode: "onBlur",
    defaultValues: {
      emailConsent: false,
      pushConsent: false,
    },
  });

  return (
    <ScrollView className="bg-white p-3">
      <View className="flex items-center justify-center py-6 px-4">
        <H1 icon={User} className="mb-4">
          Einwilligung für Benachrichtigungen
        </H1>
      </View>
      <Card className="my-2 p-3">
        <Text>
          Um Ihnen eine optimale Nutzung unserer Dienste zu ermöglichen, möchten
          wir Ihnen Benachrichtigungen zusenden. Bitte lesen Sie die folgenden
          Informationen und aktivieren Sie die gewünschten
          Benachrichtigungskanäle.
        </Text>
      </Card>
      <Card className="my-2 p-3">
        <View className="mb-5">
          <Text className="font-bold mb-2">Push-Benachrichtigungen</Text>
          <Checkbox control={control} name="pushConsent">
            Ich erlaube den Empfang von Push-Benachrichtigungen auf meinem
            Gerät.
          </Checkbox>
        </View>
        <View className="mb-5">
          <Text className="font-bold mb-2">E-Mail-Benachrichtigungen</Text>
          <Checkbox control={control} name="emailConsent">
            Ich erlaube den Empfang von E-Mail-Benachrichtigungen auf meinem
            Gerät.
          </Checkbox>
        </View>

        <View className="flex flex-row justify-between  py-4 items-center mb-5">
          <Button
            variant="secondary"
            onPress={() => {
              router.navigate("/profile");
            }}
          >
            <Text>Zurück</Text>
          </Button>
          <Button
            onPress={handleSubmit((values) => {
              console.log(values);
            })}
            variant="default"
          >
            <Text>Speichern</Text>
          </Button>
        </View>
      </Card>
    </ScrollView>
  );
}
