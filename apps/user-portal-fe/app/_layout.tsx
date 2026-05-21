import { useAppState } from "@/hooks/use-app-state";
import { useOnlineManager } from "@/hooks/use-online-manager";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { type Theme, ThemeProvider } from "@react-navigation/native";
import { AuthenticationProvider } from "@repo/auth";
import { HTTPError } from "@repo/shared";
import "@repo/tailwind-config/global.css";
import {
  NAV_THEME,
  PortalHost,
  PortalProvider,
  useColorScheme,
} from "@repo/ui";
import {
  QueryCache,
  QueryClient,
  QueryClientProvider,
  focusManager,
} from "@tanstack/react-query";
import { useFonts } from "expo-font";
import { LinearGradient } from "expo-linear-gradient";
import { Stack, router } from "expo-router";
import * as SplashScreen from "expo-splash-screen";
import { useEffect, useState } from "react";
import { type AppStateStatus, Platform, StyleSheet } from "react-native";

const LIGHT_THEME: Theme = {
  dark: false,
  colors: NAV_THEME.light,
};
const DARK_THEME: Theme = {
  dark: true,
  colors: NAV_THEME.dark,
};

export {
  // Catch any errors thrown by the Layout component.
  ErrorBoundary,
} from "expo-router";

export const unstable_settings = {
  // Ensure that reloading on `/modal` keeps a back button present.
  initialRouteName: "(tabs)",
};

// Prevent the splash screen from auto-hiding before asset loading is complete.
SplashScreen.preventAutoHideAsync();

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 2 } },
  queryCache: new QueryCache({
    onError: (error) => {
      if (error instanceof HTTPError && error.response.status === 401) {
        router.replace("/sign-in");
      }
    },
  }),
});

export default function RootLayout() {
  const [loaded, error] = useFonts({
    Roboto: require("../../../libs/ui/src/fonts/Roboto-Regular.ttf"),
    RobotoMedium: require("../../../libs/ui/src/fonts/Roboto-Medium.ttf"),
  });

  // Expo Router uses Error Boundaries to catch errors in the navigation tree.
  useEffect(() => {
    if (error) throw error;
  }, [error]);

  useEffect(() => {
    if (loaded) {
      SplashScreen.hideAsync();
    }
  }, [loaded]);

  if (!loaded) {
    return null;
  }

  return <RootLayoutNav />;
}

function onAppStateChange(status: AppStateStatus) {
  // React Query already supports in web browser refetch on window focus by default
  if (Platform.OS !== "web") {
    focusManager.setFocused(status === "active");
  }
}

function RootLayoutNav() {
  useOnlineManager();
  useAppState(onAppStateChange);
  const { colorScheme, setColorScheme, isDarkColorScheme } = useColorScheme();
  const [isColorSchemeLoaded, setIsColorSchemeLoaded] = useState(false);

  // biome-ignore lint/correctness/useExhaustiveDependencies: it should only run once on mount
  useEffect(() => {
    (async () => {
      const theme = await AsyncStorage.getItem("theme");
      if (Platform.OS === "web") {
        // Adds the background color to the html element to prevent white background on overscroll.
        document.documentElement.classList.add("bg-background");
      }
      if (!theme) {
        AsyncStorage.setItem("theme", colorScheme);
        setIsColorSchemeLoaded(true);
        return;
      }
      // const colorTheme = theme === "dark" ? "dark" : "light";
      const colorTheme = "light";
      if (colorTheme !== colorScheme) {
        setColorScheme(colorTheme);

        setIsColorSchemeLoaded(true);
        return;
      }
      setIsColorSchemeLoaded(true);
    })().finally(() => {
      SplashScreen.hideAsync();
    });
  }, []);

  if (!isColorSchemeLoaded) {
    return null;
  }

  return (
    <PortalProvider>
      <QueryClientProvider client={queryClient}>
        <AuthenticationProvider>
          <ThemeProvider value={isDarkColorScheme ? DARK_THEME : LIGHT_THEME}>
            <LinearGradient
              colors={["#3595CB", "#49A8A8", "#0F5593"]}
              start={{ x: 1, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={StyleSheet.absoluteFill}
            />
            <Stack>
              <Stack.Screen
                name="(tabs)"
                options={{
                  headerShown: false,
                }}
              />
              <Stack.Screen
                name="sign-in"
                options={{
                  headerShown: false,
                }}
              />
              <Stack.Screen name="modal" options={{ presentation: "modal" }} />
            </Stack>
            {/* <ReactQueryDevtools /> */}
          </ThemeProvider>
        </AuthenticationProvider>
      </QueryClientProvider>
      <PortalHost />
    </PortalProvider>
  );
}
