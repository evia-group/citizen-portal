import { Button } from "@repo/ui";
import { Settings2 } from "lucide-react-native";
import { Text, View } from "react-native";

export default function Archive() {
  return (
    <>
      <View className="flex flex-row justify-between  py-4 items-center ">
        <Text className="text-xl font-bold text-gray-700">
          Archivierte Dokumente
        </Text>
        <View className="flex flex-row space-x-2">
          <Button>
            <Settings2 size={24} color="white" />
          </Button>
        </View>
      </View>
      <View className="flex flex-col space-y-2">
        <View className="flex flex-row justify-between items-center py-2 hover:bg-gray-200">
          <Text className="text-base font-medium text-gray-700">
            Anmeldung einer Hundehaltung.pdf
          </Text>
        </View>
      </View>
    </>
  );
}
