import { useAuthentication, useLoggedInAuth } from "@repo/auth";
import { useMailbox } from "@repo/mailbox";
import { useProfileNotifications } from "@repo/profile";
import { LoadingIndicator } from "@repo/ui";
import { Link, Redirect, Tabs } from "expo-router";
import { CircleUser, FileText, Home, Mail, UserCog } from "lucide-react-native";
import { Platform, Pressable, Text, View } from "react-native";

export default function TabLayout() {
  const { isReady, isAuthenticated, profileId } = useAuthentication();

  if (!isReady) return <LoadingIndicator />;

  if (isReady && !isAuthenticated) {
    return <Redirect href="/sign-in" />;
  }

  if (!profileId) {
    return <LoadingIndicator />;
  }

  return (
    <Tabs
      screenOptions={{
        // Header
        headerShown: true,
        headerStyle: {
          backgroundColor: "transparent",
          borderBottomColor: "rgba(255, 255, 255,0.8)",
          borderBottomWidth: 1,
          // react-native-web deprecated the shadow* props in favour of
          // boxShadow; native keeps the original props.
          ...Platform.select({
            web: { boxShadow: "none" },
            default: { elevation: 0, shadowOpacity: 0 },
          }),
        },
        headerRight: () => {
          // biome-ignore lint/correctness/useHookAtTopLevel: this is a inline function component
          const { profileId } = useLoggedInAuth();
          // biome-ignore lint/correctness/useHookAtTopLevel: this is a inline function component
          const { data: notifications } = useProfileNotifications(profileId);
          const hasUnreadNotifications = notifications
            ? notifications.filter(
                (notification) => notification.status === "PENDING",
              )
            : [];

          return (
            <Link href="/profile" asChild>
              <Pressable className="pr-5">
                <View className="relative">
                  {hasUnreadNotifications.length > 0 ? (
                    <View className="absolute -top-1 -right-1 bg-red-500 w-5 h-5 rounded-full flex items-center justify-center">
                      <Text className="text-white text-xs">
                        {hasUnreadNotifications.length.toString()}
                      </Text>
                    </View>
                  ) : null}
                  <CircleUser size={28} color="#fff" />
                </View>
              </Pressable>
            </Link>
          );
        },

        headerTitleStyle: {
          color: "#fff",
        },

        // TabBar
        tabBarInactiveTintColor: "rgba(255, 255, 255,0.5)",
        tabBarActiveTintColor: "#fff",
        tabBarStyle: {
          backgroundColor: "transparent",
          borderTopWidth: 1,
          borderTopColor: "rgba(255, 255, 255,0.8)",
          // react-native-web deprecated the shadow* props in favour of
          // boxShadow; native keeps the original props.
          ...Platform.select({
            web: { boxShadow: "0 -3px 10px rgba(0, 0, 0, 0.4)" },
            default: {
              elevation: 0,
              shadowOpacity: 0.4,
              shadowColor: "black",
              shadowOffset: { width: 0, height: -3 },
              shadowRadius: 10,
            },
          }),
        },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: "Home",
          tabBarIcon: ({ focused }) => (
            <View className={`${focused ? "opacity-100" : "opacity-50"}`}>
              <Home size={30} color="#fff" />
            </View>
          ),
        }}
      />
      <Tabs.Screen
        name="dms/(list)"
        options={{
          title: "Dokumente",
          tabBarIcon: ({ focused }) => (
            <View className={`${focused ? "opacity-100" : "opacity-50"}`}>
              <FileText size={30} color="#fff" />
            </View>
          ),
        }}
      />
      <Tabs.Screen
        name="dms/create"
        options={{
          title: "Dokumente",
          href: null,
        }}
      />
      <Tabs.Screen
        name="services"
        options={{
          title: "Services",
          tabBarIcon: ({ focused }) => (
            <View className={`${focused ? "opacity-100" : "opacity-50"}`}>
              <UserCog size={30} color="#fff" />
            </View>
          ),
        }}
      />
      <Tabs.Screen
        name="mailbox"
        options={{
          title: "Mailbox",
          tabBarIcon: ({ focused }) => {
            // biome-ignore lint/correctness/useHookAtTopLevel: this is a inline function component
            const { profileId } = useLoggedInAuth();
            // biome-ignore lint/correctness/useHookAtTopLevel: this is a inline function component
            const { data: mailbox } = useMailbox(profileId);
            const hasUnreadMails = mailbox?.some(
              (mail) => mail.status === "PENDING",
            );

            return (
              <View className="relative">
                <View className={`${focused ? "opacity-100" : "opacity-50"}`}>
                  <Mail size={30} color="#fff" />
                </View>
                {hasUnreadMails ? (
                  <View className="absolute -top-1 -right-1 bg-red-500 w-3 h-3 rounded-full" />
                ) : null}
              </View>
            );
          },
        }}
      />
      <Tabs.Screen
        name="profile/data"
        options={{
          title: "Meine Profildaten",
          href: null, // hide it from the tabs
        }}
      />
      <Tabs.Screen
        name="profile/index"
        options={{
          title: "Mein Profil",
          href: null, // hide it from the tabs
        }}
      />
      <Tabs.Screen
        name="profile/municipality"
        options={{
          title: "Meine Kommune",
          href: null, // hide it from the tabs
        }}
      />
      <Tabs.Screen
        name="profile/notifications"
        options={{
          title: "Benachrichtungseinstellungen",
          href: null, // hide it from the tabs
        }}
      />
      <Tabs.Screen
        name="profile/my-notifications"
        options={{
          title: "Benachrichtigungen",
          href: null, // hide it from the tabs
        }}
      />
      <Tabs.Screen
        name="profile/payments"
        options={{
          title: "Zahlungs- & Steuerinformationen",
          href: null, // hide it from the tabs
        }}
      />
    </Tabs>
  );
}
