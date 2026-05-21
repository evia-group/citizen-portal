import { useLoggedInAuth } from "@repo/auth";
import { useProfileNotifications } from "@repo/profile";
import {
  Button,
  Dialog,
  DialogContent,
  DialogTrigger,
  H1,
  Text,
  useDisclosure,
} from "@repo/ui";
import { Link, router } from "expo-router";
import { User } from "lucide-react-native";
import type { ReactNode } from "react";
import { ScrollView, View } from "react-native";

// biome-ignore lint/suspicious/noExplicitAny: don't know how to type the href prop (https://docs.expo.dev/router/reference/typed-routes/)
function ProfileLink({ href, children }: { href: any; children: ReactNode }) {
  return (
    <Link
      href={href}
      className="text-primary border rounded border-primary px-4 py-3 text-center"
    >
      {children}
    </Link>
  );
}

export default function ProfileScreen() {
  const { logout, profileId } = useLoggedInAuth();
  const [modalVisible, { open, close }] = useDisclosure(false);

  const { data } = useProfileNotifications(profileId);
  const pendingNotifications = data
    ? data.filter((notification) => notification.status === "PENDING")
    : [];

  return (
    <ScrollView className="bg-white">
      <View className="flex items-center justify-center py-6 px-4">
        <H1 icon={User} className="mb-4">
          Mein Profil
        </H1>

        <View className="flex flex-col gap-4 mb-7">
          <ProfileLink href="/profile/data">Meine Profildaten</ProfileLink>
          <ProfileLink href="/profile/municipality">Meine Kommune</ProfileLink>
          <ProfileLink href="/profile/payments">
            Zahlungs- & Steuerinformationen
          </ProfileLink>
          <ProfileLink href="/profile/my-notifications">
            Meine Benachrichtigungen ({pendingNotifications.length})
          </ProfileLink>
          <ProfileLink href="/profile/notifications">
            Benachrichtungseinstellungen
          </ProfileLink>
        </View>

        <Dialog
          open={modalVisible}
          onOpenChange={(value) => (value ? open() : close())}
        >
          <DialogTrigger asChild>
            <Button>
              <Text>Abmelden</Text>
            </Button>
          </DialogTrigger>
          <DialogContent>
            <View className="flex flex-col gap-4">
              <Text>Sind Sie sich sicher, dass Sie sich abmelden möchten?</Text>

              <View className="flex flex-row justify-between">
                <Button variant="secondary" onPress={() => close()}>
                  <Text>Abbrechen</Text>
                </Button>
                <Button
                  variant="destructive"
                  onPress={async () => {
                    await logout();

                    router.replace("/sign-in");
                  }}
                >
                  <Text>Abmelden</Text>
                </Button>
              </View>
            </View>
          </DialogContent>
        </Dialog>
      </View>
    </ScrollView>
  );
}
