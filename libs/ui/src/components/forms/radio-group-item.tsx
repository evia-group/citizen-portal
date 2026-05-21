import { type ReactNode, useContext, useId } from "react";
import { Pressable, View } from "react-native";
import { RadioGroupItem as StyledRadioGroupItem } from "../ui/radio-group";
import { Text } from "../ui/text";
import { RadioGroupContext } from "./radio-group";

interface Props {
  value: string;
  children: ReactNode;
}

export function RadioGroupItem({ value, children }: Props) {
  const id = useId();
  const context = useContext(RadioGroupContext);
  return (
    <View className="flex flex-row items-center">
      <StyledRadioGroupItem value={value} aria-labelledby={id} />
      <Pressable onPress={() => context.setRadioGroupValue(value)}>
        <Text className="pl-2" id={id}>
          {children}
        </Text>
      </Pressable>
    </View>
  );
}
