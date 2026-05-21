import { Text } from "react-native";

export function Debug({ data }: { data: unknown }) {
  if (process.env.NODE_ENV !== "development") {
    return null;
  }
  return <Text>{JSON.stringify(data, undefined, 2)}</Text>;
}
