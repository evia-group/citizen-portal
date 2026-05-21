import { Link, Stack } from "expo-router";
import { Text, View } from "react-native";

export default function NotFoundScreen() {
  return (
    <>
      <Stack.Screen options={{ title: "Oops!" }} />
      <View className="flex items-center justify-center p-5">
        <Text className="font-bold text-xl">This screen doesn't exist.</Text>

        <Link href="/" className="mt-4 px-4">
          <Text className="text-sm text-teal-800">Go to home screen!</Text>
        </Link>
      </View>
    </>
  );
}
