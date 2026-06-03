import { router, Stack } from "expo-router";
import { Header } from "@/components/header";

export default function MailboxLayout() {
  return (
    <Stack
      initialRouteName="index"
      screenOptions={{
        header({ route, options }) {
          if (route.name === "index") {
            return null;
          }
          return (
            <Header
              onPress={() => router.navigate("/profile/my-notifications/")}
            >
              {options.title || "Nachricht"}
            </Header>
          );
        },
      }}
    />
  );
}
