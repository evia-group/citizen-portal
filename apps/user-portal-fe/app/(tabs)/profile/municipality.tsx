import { useLoggedInAuth } from "@repo/auth";
import {
  LocationForm,
  useLocationList,
  useProfile,
  useUpdateProfile,
} from "@repo/profile";
import { Dialog, DialogContent, H1, Text, useDisclosure } from "@repo/ui";
import { router } from "expo-router";
import { CircleCheck, User } from "lucide-react-native";
import { ScrollView, View } from "react-native";

export default function ProfileMunicipalityScreen() {
  const { profileId } = useLoggedInAuth();
  const { data, isLoading, isError, error } = useProfile(profileId, {
    staleTime: Number.POSITIVE_INFINITY,
  });
  const { data: locations, isLoading: isLoadingLocations } = useLocationList();
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
            Meine Kommune
          </H1>

          {(isLoading || isLoadingLocations) && <Text>Loading...</Text>}
          {isError && <Text>Error... {error.message}</Text>}

          {data && locations ? (
            <LocationForm
              profile={data}
              locations={locations}
              onSubmit={async (location) => {
                await mutation.mutateAsync({
                  ...data,
                  location: location,
                });
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
