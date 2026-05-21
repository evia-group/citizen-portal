import { Card } from "@repo/ui";
import { useState } from "react";
import { Dimensions } from "react-native";
import Carousel from "react-native-reanimated-carousel";

type Props<T> = {
  renderItem: (item: T) => React.ReactElement;
  data: T[];
};

export function DashboardCarousel<T>({ renderItem, data }: Props<T>) {
  const [height, setHeight] = useState(100);
  const width = Dimensions.get("window").width;

  return (
    <Carousel
      loop={false}
      width={width - 16}
      height={height}
      style={{
        marginLeft: -8,
        marginRight: -8,
      }}
      data={data}
      scrollAnimationDuration={1000}
      renderItem={({ item }) => (
        <Card
          className="mx-2"
          onLayout={(event) => {
            const { layout } = event.nativeEvent;
            if (layout) {
              setHeight((h) => Math.max(h, layout.height));
            }
          }}
        >
          {renderItem(item)}
        </Card>
      )}
    />
  );
}
