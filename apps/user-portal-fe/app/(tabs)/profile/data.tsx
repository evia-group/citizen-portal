import { useLoggedInAuth } from "@repo/auth";
import { ProfileForm, useProfile, useUpdateProfile } from "@repo/profile";
import {
  Dialog,
  DialogContent,
  H1,
  LoadingIndicator,
  Text,
  useDisclosure,
} from "@repo/ui";
import { router } from "expo-router";
import { CircleCheck, User } from "lucide-react-native";
import { ScrollView, View } from "react-native";

export default function ProfileDataScreen() {
  const { profileId } = useLoggedInAuth();
  const { data, isLoading, isError, error } = useProfile(profileId, {
    staleTime: Number.POSITIVE_INFINITY,
  });
  const mutation = useUpdateProfile(profileId);
  const [modalVisible, { open, close }] = useDisclosure(false);

  return (
    <>
      <Dialog
        open={modalVisible}
        onOpenChange={(value) => (value ? open() : close())}
      >
        <DialogContent>
          <View className="flex flex-row gap-4 items-center justify-center">
            <CircleCheck size={28} color="green" />
            <Text>Änderungen wurden erfolgreich gespeichert!</Text>
          </View>
        </DialogContent>
      </Dialog>
      <ScrollView className="bg-white">
        <View className="flex items-center justify-center py-6 px-4">
          <H1 icon={User} className="mb-4">
            Meine Profildaten
          </H1>

          {isLoading && <LoadingIndicator />}
          {isError && <Text>Error... {error.message}</Text>}

          {data ? (
            <ProfileForm
              profile={data}
              onSubmit={async (profile) => {
                await mutation.mutateAsync(profile);
                open();
                setTimeout(() => {
                  close();
                }, 2_000);
              }}
              onBack={() => router.navigate("/profile/")}
            />
          ) : null}
        </View>
      </ScrollView>
    </>
  );
}
