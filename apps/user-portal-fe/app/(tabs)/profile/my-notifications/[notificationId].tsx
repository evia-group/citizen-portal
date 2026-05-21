import {
  useProfileNotification,
  useUpdateProfileNotification,
} from "@repo/profile";
import { Separator, Text, formatDate } from "@repo/ui";
import { Stack, useLocalSearchParams } from "expo-router";
import { useEffect } from "react";
import { ScrollView, View } from "react-native";

export default function NotificationId() {
  const { notificationId } = useLocalSearchParams();
  const { data } = useProfileNotification(Number(notificationId ?? "1"), 1);
  const updateNotificationStatus = useUpdateProfileNotification(
    Number(notificationId ?? "1"),
    1,
  );

  // biome-ignore lint/correctness/useExhaustiveDependencies: mutation variable is "mutated" too often and triggers the effect in a loop
  useEffect(() => {
    if (data && data.status !== "VIEWED") {
      updateNotificationStatus.mutate();
    }
  }, [data]);

  return (
    <ScrollView className="bg-white p-4">
      <Stack.Screen
        options={{
          title: "Nachricht",
        }}
      />
      {data ? (
        <View>
          <View className="flex flex-row justify-between">
            <Text className="font-robotoMedium">{data.source}</Text>
            <Text className="text-gray-500 text-sm">
              {formatDate(data.createdDate ?? "")}
            </Text>
          </View>
          <Separator className="my-3" />
          <View>
            <Text>{data.message}</Text>
          </View>
        </View>
      ) : null}
    </ScrollView>
  );
}
