import { useId } from "react";
import {
  type FieldValues,
  type UseControllerProps,
  useController,
} from "react-hook-form";
import { type InputModeOptions, Text, View } from "react-native";
import { Textarea as StyledTextarea } from "../ui/textarea";

interface Props<TFieldValues extends FieldValues>
  extends UseControllerProps<TFieldValues> {
  /** Label */
  label?: string;
  /** If you don't need a visible label, use aria-label for accessibility */
  "aria-label"?: string;
  /** Placeholder text */
  placeholder?: string;
  /** Additional class names */
  className?: string;
  /** Input type */
  type?: InputModeOptions;
  /** If true, the input is read-only */
  readOnly?: boolean;
}

export function Textarea<TFieldValues extends FieldValues>(
  props: Props<TFieldValues>,
) {
  const id = useId();
  const {
    label,
    "aria-label": ariaLabel,
    placeholder,
    className,
    type,
    readOnly,
    ...controllerProps
  } = props;
  const { field, fieldState } = useController(controllerProps);

  if (process.env.NODE_ENV === "development") {
    if (!label && !ariaLabel) {
      console.warn(
        "If you do not provide a visible label, you must specify an aria-label attribute for accessibility",
      );
    }
  }

  return (
    <View className={className}>
      {label ? (
        <Text id={id} className="font-robotoMedium text-sm mb-1">
          {label}
        </Text>
      ) : null}
      <StyledTextarea
        className={fieldState.error ? "border-destructive" : undefined}
        placeholder={placeholder}
        value={field.value}
        onChangeText={field.onChange}
        onBlur={field.onBlur}
        ref={field.ref}
        readOnly={readOnly || field.disabled}
        aria-disabled={field.disabled}
        aria-labelledby={label ? id : undefined}
        inputMode={type ?? "text"}
        aria-label={label || ariaLabel}
      />
      {fieldState.error && (
        <Text className="text-destructive">{fieldState.error.message}</Text>
      )}
    </View>
  );
}
