import { Text, View } from "react-native";

export default function ModalScreen() {
  return (
    <View className="flex items-center justify-center">
      <Text className="font-bold mb-7">Modal</Text>
      <View className="mb-7 h-px bg-gray-900 w-4/5" />
      <Text>Text</Text>
    </View>
  );
}
