import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Text,
  useDisclosure,
} from "@repo/ui";
import { Link, router, Slot } from "expo-router";
import { Bot, Settings2 } from "lucide-react-native";
import { ScrollView, View } from "react-native";

export default function DmsLayout() {
  const [modalVisible, { open, close }] = useDisclosure(false);

  return (
    <>
      <Dialog
        open={modalVisible}
        onOpenChange={(value) => (value ? open() : close())}
      >
        <DialogContent>
          <DialogTitle className="sr-only">Dokument wird gelesen</DialogTitle>
          <View className="flex flex-column gap-4 items-center justify-center">
            <Text>Das Dokument wird gelesen....</Text>
            <Bot size={36} color="black" />
          </View>
        </DialogContent>
      </Dialog>
      <ScrollView className="bg-white">
        <View className="flex items-center justify-center py-6 px-4">
          <View className="flex flex-col w-full h-screen">
            <View className="flex flex-row justify-between  py-4 items-center mb-5">
              <Text className="text-xl font-bold text-gray-700">Dokumente</Text>
              <View className="flex flex-row space-x-2">
                <Button>
                  <Settings2 size={24} color="white" />
                </Button>
              </View>
            </View>
            <View className="flex flex-row justify-between py-4 items-center mb-5">
              <Link href="/dms" asChild>
                <Button variant="outline">
                  <Text>Meine Dokumente</Text>
                </Button>
              </Link>
              <Link href="/dms/archive" asChild>
                <Button variant="outline">
                  <Text>Archivierte Dokumente</Text>
                </Button>
              </Link>
            </View>
            <View className="flex flex-row justify-center  py-4 items-center ">
              <Button
                onPress={() => {
                  open();
                  setTimeout(() => {
                    router.push("/dms/create");
                    close();
                  }, 4_000);
                }}
              >
                <Text>Dokument/e hochladen</Text>
              </Button>
            </View>
            <Slot />
          </View>
        </View>
      </ScrollView>
    </>
  );
}
