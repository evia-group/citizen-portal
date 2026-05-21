import { ActivityIndicator, View } from "react-native";

export function LoadingIndicator() {
  return (
    <View className="flex justify-center items-center">
      <ActivityIndicator size="large" />
    </View>
  );
}
