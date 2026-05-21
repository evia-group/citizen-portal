import AsyncStorage from "@react-native-async-storage/async-storage";
import { useAuthentication } from "@repo/auth";
import {
  Button,
  Card,
  Checkbox,
  H1,
  LoadingIndicator,
  Logo,
  Text,
} from "@repo/ui";
import { Redirect } from "expo-router";
import { User } from "lucide-react-native";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { ScrollView, View } from "react-native";

export default function SignIn() {
  const { isReady, isAuthenticated, isLoggingIn, loginError, login } =
    useAuthentication();
  const [previouslyLoggedIn, setPreviouslyLoggedIn] = useState<boolean | null>(
    null,
  );

  useEffect(() => {
    retrievePreviouslyLoggedInState();
  }, []);

  const { handleSubmit, control } = useForm<{
    consent: boolean;
  }>({
    mode: "onBlur",
    defaultValues: {
      consent: false,
    },
  });

  const onSubmit = (values: { consent: boolean }) => {
    if (values.consent) {
      setPreviouslyLoggedIn(true);
      savePreviouslyLoggedInState(true);
    }
  };

  if (!isReady) return <LoadingIndicator />;

  if (isAuthenticated) {
    return <Redirect href="/" />;
  }

  if (previouslyLoggedIn === false) {
    return (
      <ScrollView className="bg-white p-3">
        <View className="flex items-center justify-center py-6 px-4">
          <H1 icon={User} className="mb-4">
            Einwilligungserklärung zur Verarbeitung von personenbezogenen Daten
          </H1>
        </View>
        <Card className="my-2 p-3">
          <Text>
            Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam
            nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam
            erat, sed diam voluptua. At vero eos et accusam et justo duo dolores
            et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est
            Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur
            sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
            et dolore magna aliquyam erat, sed diam voluptua. At vero eos et
            accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,
            no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum
            dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod
            tempor invidunt ut labore et dolore magna aliquyam erat, sed diam
            voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
            Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum
            dolor sit amet. Duis autem vel eum iriure dolor in hendrerit in
            vulputate velit esse molestie consequat, vel illum dolore eu feugiat
            nulla facilisis at vero eros et Lorem ipsum dolor sit amet,
            consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt
            ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero
            eos et accusam et
          </Text>
        </Card>
        <View className="mb-5">
          <Text className="font-bold mb-2">Push-Benachrichtigungen</Text>
          <Checkbox control={control} name="consent">
            Ich stimme zu, dass meine persönlichen Daten für die Nutzung gemäß
            den Datenschutzrichtlinien verarbeitet werden dürfen.
          </Checkbox>
        </View>

        <View className="flex flex-row justify-between  py-4 items-center mb-5">
          <Button onPress={handleSubmit(onSubmit)} variant="default">
            <Text>Speichern</Text>
          </Button>
        </View>
      </ScrollView>
    );
  }

  return (
    <View className="flex flex-col items-center py-10 px-6">
      <View className="mb-7">
        <Logo />
      </View>
      <Text
        role="heading"
        aria-level="1"
        className="web:scroll-m-20 text-2xl lg:text-4xl text-white font-robotoMedium tracking-tight web:select-text mb-4"
      >
        Willkommen!
      </Text>
      <Button
        onPress={() => {
          login();
        }}
        disabled={isLoggingIn}
      >
        <Text>Anmelden</Text>
      </Button>
      {isLoggingIn && <LoadingIndicator />}
      {loginError && (
        <Text className="text-destructive mt-2 text-center">{loginError}</Text>
      )}
    </View>
  );

  async function savePreviouslyLoggedInState(value: boolean) {
    try {
      await AsyncStorage.setItem("previouslyLoggedIn", JSON.stringify(value));
    } catch (error) {
      console.error("Error saving previouslyLoggedIn state:", error);
    }
  }

  async function retrievePreviouslyLoggedInState() {
    try {
      const value = await AsyncStorage.getItem("previouslyLoggedIn");
      if (value !== null) {
        setPreviouslyLoggedIn(JSON.parse(value));
      } else {
        setPreviouslyLoggedIn(false);
      }
    } catch (error) {
      console.error("Error retrieving previouslyLoggedIn state:", error);
    }
  }
}
