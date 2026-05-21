import { type ReactNode, useId } from "react";
import {
  type FieldValues,
  type UseControllerProps,
  useController,
} from "react-hook-form";
import { Pressable, View } from "react-native";
import { cn } from "~/utils/cn";
import { Checkbox as StyledCheckbox } from "../ui/checkbox";
import { Text } from "../ui/text";

interface Props<TFieldValues extends FieldValues>
  extends UseControllerProps<TFieldValues> {
  /** Label */
  children: ReactNode;
  /** Additional class names */
  className?: string;
  /** If true, the input is read-only */
  readOnly?: boolean;
}

export function Checkbox<TFieldValues extends FieldValues>(
  props: Props<TFieldValues>,
) {
  const id = useId();
  const { children, className, readOnly, ...controllerProps } = props;
  const { field, fieldState } = useController(controllerProps);

  return (
    <View
      className={cn(className, "flex flex-row gap-3 items-center max-w-full")}
    >
      <StyledCheckbox
        className={fieldState.error ? "border-destructive" : undefined}
        checked={field.value}
        onCheckedChange={field.onChange}
        onBlur={field.onBlur}
        ref={field.ref}
        disabled={readOnly || field.disabled}
        aria-disabled={field.disabled}
        aria-labelledby={id}
      />
      <Pressable
        onPress={() => {
          field.onChange(!field.value);
        }}
        className="shrink"
      >
        <Text id={id} className="whitespace-normal">
          {children}
        </Text>
      </Pressable>
      {fieldState.error && (
        <Text className="text-destructive">{fieldState.error.message}</Text>
      )}
    </View>
  );
}
