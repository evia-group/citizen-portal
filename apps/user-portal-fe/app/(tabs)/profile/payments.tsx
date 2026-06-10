import { useLoggedInAuth } from "@repo/auth";
import { PaymentsForm, useProfile, useUpdateProfile } from "@repo/profile";
import {
  Dialog,
  DialogContent,
  DialogTitle,
  H1,
  useDisclosure,
} from "@repo/ui";
import { router } from "expo-router";
import { CircleCheck, User } from "lucide-react-native";
import { ScrollView, Text, View } from "react-native";

export default function ProfilePaymentsScreen() {
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
          <DialogTitle className="sr-only">Änderungen gespeichert</DialogTitle>
          <View className="flex flex-row gap-4 items-center justify-center">
            <CircleCheck size={28} color="green" />
            <Text>Änderungen wurden erfolgreich gespeichert!</Text>
          </View>
        </DialogContent>
      </Dialog>
      <ScrollView className="bg-white">
        <View className="flex items-center justify-center py-6 px-4">
          <H1 icon={User} className="mb-4">
            Zahlungs- & Steuerinformationen
          </H1>

          {isLoading && <Text>Loading...</Text>}
          {isError && <Text>Error... {error.message}</Text>}

          {data && (
            <PaymentsForm
              profile={data}
              onSubmit={async (payment) => {
                await mutation.mutateAsync({
                  ...data,
                  paymentData: payment,
                });
                open();
                setTimeout(() => {
                  close();
                }, 2_000);
              }}
              onBack={() => router.navigate("/profile")}
            />
          )}
        </View>
      </ScrollView>
    </>
  );
}
