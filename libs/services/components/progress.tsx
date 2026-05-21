import { Progress, Text } from "@repo/ui";
import { View } from "react-native";

interface Props {
  /** current step (starting from `1`) */
  step: number;
  /** overall count of all steps */
  count: number;
  /** title of the current progress step */
  title: string;
}

export function ProgressSteps({ step, count, title }: Props) {
  return (
    <View>
      <View className="flex flex-row justify-between mb-2">
        <Text className="text-sm">{title}</Text>
        <Text className="text-sm">
          Schritt {step} von {count}
        </Text>
      </View>
      <Progress value={(step / count) * 100} max={100} className="mb-4" />
    </View>
  );
}
