import { type ReactNode, createContext, useId } from "react";
import {
  type FieldValues,
  type UseControllerProps,
  useController,
} from "react-hook-form";
import { Text, View } from "react-native";
import { RadioGroup as StyledRadioGroup } from "../ui/radio-group";

interface InputProps<TFieldValues extends FieldValues>
  extends UseControllerProps<TFieldValues> {
  /** Label */
  label?: string;
  /** Additional class names */
  className?: string;
  /** If true, the input is read-only */
  readOnly?: boolean;
  /** Radio Items as children */
  children: ReactNode;
}

export const RadioGroupContext = createContext<{
  setRadioGroupValue: (value: string) => void;
}>({
  setRadioGroupValue: () => {},
});

export function RadioGroup<TFieldValues extends FieldValues>(
  props: InputProps<TFieldValues>,
) {
  const id = useId();
  const { label, className, readOnly, children, ...controllerProps } = props;
  const { field, fieldState } = useController(controllerProps);

  return (
    <View className={className}>
      {label ? (
        <Text id={id} className="font-robotoMedium text-sm mb-1">
          {label}
        </Text>
      ) : null}
      <StyledRadioGroup
        className={fieldState.error ? "border-destructive" : undefined}
        value={field.value}
        onValueChange={field.onChange}
        ref={field.ref}
        disabled={readOnly || field.disabled}
        aria-disabled={field.disabled}
        aria-labelledby={label ? id : undefined}
      >
        <RadioGroupContext.Provider
          value={{ setRadioGroupValue: field.onChange }}
        >
          {children}
        </RadioGroupContext.Provider>
      </StyledRadioGroup>
      {fieldState.error && (
        <Text className="text-destructive">{fieldState.error.message}</Text>
      )}
    </View>
  );
}
