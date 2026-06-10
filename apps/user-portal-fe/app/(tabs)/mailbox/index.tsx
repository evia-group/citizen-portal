import { useLoggedInAuth } from "@repo/auth";
import { useMailbox } from "@repo/mailbox";
import { Button, Card, cn, formatDate, Text } from "@repo/ui";
import { router } from "expo-router";
import { Menu, Plus } from "lucide-react-native";
import { FlatList, Pressable, View } from "react-native";

export default function MailboxScreen() {
  const { profileId } = useLoggedInAuth();
  const { data } = useMailbox(profileId);

  return (
    <>
      <View className="p-4 h-full">
        <View className="flex flex-row justify-end space-x-2">
          <Button>
            <Menu size={24} color="white" />
          </Button>
        </View>

        <Card className="my-2 flex-shrink">
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
                        router.push(`/mailbox/${item.id}`);
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
                          {formatDate(item.sendAt)}
                        </Text>
                      </View>
                      <Text
                        className={cn({
                          "font-robotoMedium": isUnread,
                        })}
                      >
                        von: {item.sender}
                      </Text>
                    </Pressable>
                  </View>
                );
              }}
            />
          ) : null}
        </Card>

        <View className="flex flex-row justify-center  py-4 items-center ">
          <Button onPress={() => {}} className="flex flex-row gap-2">
            <Plus size={24} color="white" />
            <Text>Neue E-Mail</Text>
          </Button>
        </View>
      </View>
    </>
  );
}
