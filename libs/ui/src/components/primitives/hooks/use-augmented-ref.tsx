import * as React from "react";

interface AugmentRefProps<T> {
  ref: React.Ref<T>;
  // biome-ignore lint/suspicious/noExplicitAny: copied from react-native-reusable
  methods?: Record<string, (...args: any[]) => any>;
  // biome-ignore lint/suspicious/noExplicitAny: copied from react-native-reusable
  deps?: any[];
}

export function useAugmentedRef<T>({
  ref,
  methods,
  deps = [],
}: AugmentRefProps<T>) {
  const augmentedRef = React.useRef<T>(null);
  // biome-ignore lint/correctness/useExhaustiveDependencies: copied from react-native-reusable
  React.useImperativeHandle(
    ref,
    () => {
      if (typeof augmentedRef === "function" || !augmentedRef?.current) {
        return {} as T;
      }
      return {
        ...augmentedRef.current,
        ...methods,
      };
    },
    deps,
  );
  return augmentedRef;
}
