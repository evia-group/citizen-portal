import { DogApplicationProvider } from "@repo/services";
import { Stack } from "expo-router";
import { Header } from "@/components/header";

export const unstable_settings = {
  // Ensure any route can link back to `/`
  initialRouteName: "index",
};

export default function ServicesLayout() {
  return (
    <DogApplicationProvider>
      <Stack
        screenOptions={{
          animation: "default",
          header({ navigation, options }) {
            return (
              <Header onPress={() => navigation.goBack()}>
                {options.title || "Alle Dienste"}
              </Header>
            );
          },
          contentStyle: { backgroundColor: "white" },
        }}
        initialRouteName="index"
      />
    </DogApplicationProvider>
  );
}
