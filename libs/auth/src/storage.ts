import AsyncStorage from "@react-native-async-storage/async-storage";
import { TokenResponse } from "expo-auth-session";

const KEY = "__token__";

export async function getItem(): Promise<TokenResponse | null> {
  let value: string | null = null;
  // if (Platform.OS === "web") {
  value = await AsyncStorage.getItem(KEY);
  // } else {
  // value = await SecureStore.getItemAsync(KEY);
  // }

  if (!value) {
    return null;
  }
  return new TokenResponse(JSON.parse(value));
}

export async function setItem(value: TokenResponse | null) {
  // if (Platform.OS === "web") {
  if (value === null) {
    return AsyncStorage.removeItem(KEY);
  }
  return AsyncStorage.setItem(KEY, JSON.stringify(value?.getRequestConfig()));
  // }
  // return SecureStore.setItemAsync(
  // KEY,
  // JSON.stringify(value?.getRequestConfig()),
  // );
}
