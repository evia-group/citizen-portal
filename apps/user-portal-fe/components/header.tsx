import { Text } from "@repo/ui";
import { ChevronLeft } from "lucide-react-native";
import { Image, Pressable, View } from "react-native";

type Props = {
  children: React.ReactNode;
  more?: React.ReactNode;
  onPress?: () => void;
};

export function Header({ children, more, onPress }: Props) {
  return (
    <View className="px-5 py-5 relative">
      <Image
        source={require("../assets/images/round-left.png")}
        className="absolute -bottom-[1px] left-0"
        style={{ width: 12, height: 12 }}
        aria-hidden
      />
      <Image
        source={require("../assets/images/round-right.png")}
        className="absolute -bottom-[1px] right-0"
        style={{ width: 12, height: 12 }}
        aria-hidden
      />
      <Pressable onPress={() => onPress?.()}>
        <View className="flex flex-row items-center">
          <View className="flex flex-row justify-center items-center h-7 w-7 mr-2 relative">
            <ChevronLeft color="#fff" />
            <View className="absolute left-0 top-0 bg-white opacity-50 rounded-full w-full h-full" />
          </View>
          <Text className="text-white font-bold">{children}</Text>
        </View>
      </Pressable>
      {more}
    </View>
  );
}
