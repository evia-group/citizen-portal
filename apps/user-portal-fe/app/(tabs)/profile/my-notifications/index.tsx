import { useLoggedInAuth } from "@repo/auth";
import { useProfileNotifications } from "@repo/profile";
import { Button, Card, cn, formatDate, Text } from "@repo/ui";
import { router } from "expo-router";
import { Menu } from "lucide-react-native";
import { FlatList, Pressable, View } from "react-native";

export default function MyNotifications() {
  const { profileId } = useLoggedInAuth();
  const { data } = useProfileNotifications(profileId);

  return (
    <View className="p-4">
      <View className="flex flex-row justify-end space-x-2">
        <Button>
          <Menu size={24} color="white" />
        </Button>
      </View>

      <Card className="my-2">
        <Text className="font-robotoMedium mb-2 p-2">Heute</Text>
        {data ? (
          <FlatList
            data={data}
            keyExtractor={(item) => item.id.toString()}
            ItemSeparatorComponent={() => (
              <View className="h-px bg-border my-1" />
            )}
            renderItem={({ item }) => {
              const isUnread = item.status === "PENDING";
              return (
                <View className="w-full">
                  <Pressable
                    className={cn("flex flex-row justify-between p-2", {
                      "border-l-2 border-primary": isUnread,
                    })}
                    onPress={() => {
                      router.push(`/profile/my-notifications/${item.id}`);
                    }}
                  >
                    <View className="flex flex-col ">
                      <Text
                        className={cn("font-robotoMedium", {
                          "text-primary": isUnread,
                        })}
                      >
                        {item.subject}
                      </Text>
                      <Text
                        className={cn("text-gray-500 text-sm", {
                          "text-primary": isUnread,
                        })}
                      >
                        {formatDate(item.createdDate ?? "")}
                      </Text>
                    </View>
                    <Text
                      className={cn({
                        "font-robotoMedium": isUnread,
                      })}
                    >
                      {item.source}
                    </Text>
                  </Pressable>
                </View>
              );
            }}
          />
        ) : null}
      </Card>
    </View>
  );
}
