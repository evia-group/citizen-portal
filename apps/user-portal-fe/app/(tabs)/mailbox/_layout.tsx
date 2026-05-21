import { Header } from "@/components/header";
import { Stack, router } from "expo-router";

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
            <Header onPress={() => router.navigate("/mailbox/")}>
              {options.title || "Mailbox"}
            </Header>
          );
        },
      }}
    />
  );
}
