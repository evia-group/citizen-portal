import { useId } from "react";
import {
  type FieldValues,
  type UseControllerProps,
  useController,
} from "react-hook-form";
import { Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import {
  type Option,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
  Select as StyledSelect,
} from "~/components/ui/select";

interface SelectProps<TFieldValues extends FieldValues>
  extends UseControllerProps<TFieldValues> {
  label: string;
  placeholder?: string;
  items: NonNullable<Option>[];
  className?: string;
  readOnly?: boolean;
}

export function Select<TFieldValues extends FieldValues>(
  props: SelectProps<TFieldValues>,
) {
  const id = useId();
  const { label, placeholder, className, items, readOnly, ...controllerProps } =
    props;
  const { field, fieldState } = useController(controllerProps);

  const insets = useSafeAreaInsets();
  const contentInsets = {
    top: insets.top,
    bottom: insets.bottom,
    left: 12,
    right: 12,
  };

  return (
    <View className={className}>
      <Text id={id} className="font-robotoMedium text-sm mb-1">
        {label}
      </Text>
      <StyledSelect
        value={{
          value: field.value,
          label: items.find((i) => i.value === field.value)?.label ?? "",
        }}
        onValueChange={(option) => field.onChange(option?.value)}
        onOpenChange={(isOpen) => {
          if (!isOpen) {
            field.onBlur();
          }
        }}
        aria-labelledby={id}
        ref={field.ref}
        disabled={readOnly || field.disabled}
        aria-disabled={readOnly || field.disabled}
        className={fieldState.error ? "border-destructive" : undefined}
      >
        <SelectTrigger className="w-full" disabled={readOnly || field.disabled}>
          <SelectValue
            className="text-foreground text-sm native:text-lg"
            placeholder={placeholder ?? "Bitte auswählen..."}
          />
        </SelectTrigger>
        <SelectContent insets={contentInsets} className="w-[250px]">
          <SelectGroup>
            {items.filter(Boolean).map((option) => (
              <SelectItem
                label={option.label}
                value={option.value}
                key={option.value}
              >
                {option.label}
              </SelectItem>
            ))}
          </SelectGroup>
        </SelectContent>
      </StyledSelect>
      {fieldState.error && (
        <Text className="text-destructive">{fieldState.error.message}</Text>
      )}
    </View>
  );
}
