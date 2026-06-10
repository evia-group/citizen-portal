import type { LucideIcon as LucideIconWeb } from "lucide-react";
import type { LucideIcon as LucideIconNative } from "lucide-react-native";
import * as React from "react";
import { Platform, Text as RNText, View as RNView } from "react-native";
import * as Slot from "~/components/primitives/slot";
import type {
  SlottableTextProps,
  SlottableViewProps,
  TextRef,
  ViewRef,
} from "~/components/primitives/types";
import { cn } from "~/utils/cn";
import { Separator } from "./separator";

const H1 = React.forwardRef<
  ViewRef,
  SlottableViewProps & {
    icon?: LucideIconNative | LucideIconWeb | React.ElementType;
  }
>(({ className, asChild = false, ...props }, ref) => {
  const Component = asChild ? Slot.View : RNView;
  const Icon = props.icon;

  return (
    <Component
      {...props}
      className={cn("flex flex-col items-center", className)}
      ref={ref}
    >
      <RNView className="flex flex-row items-center gap-2 mb-4">
        {Icon && <Icon size={28} color="#1B6E98" />}
        <RNText
          role="heading"
          aria-level="1"
          className="web:scroll-m-20 text-2xl lg:text-4xl text-primary font-robotoMedium tracking-tight web:select-text"
          {...(props as SlottableTextProps)}
        />
      </RNView>

      <Separator className="w-24 bg-primary h-[2px]" />
    </Component>
  );
});

H1.displayName = "H1";

const H2 = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        role="heading"
        aria-level="2"
        className={cn(
          "web:scroll-m-20 font-robotoMedium text-lg lg:text-2xl text-primary first:mt-0 web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

H2.displayName = "H2";

const H3 = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        role="heading"
        aria-level="3"
        className={cn(
          "web:scroll-m-20 text-base text-foreground font-robotoMedium web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

H3.displayName = "H3";

// const H4 = React.forwardRef<TextRef, SlottableTextProps>(
//   ({ className, asChild = false, ...props }, ref) => {
//     const Component = asChild ? Slot.Text : RNText;
//     return (
//       <Component
//         role="heading"
//         aria-level="4"
//         className={cn(
//           "web:scroll-m-20 text-xl text-foreground font-semibold tracking-tight web:select-text",
//           className,
//         )}
//         ref={ref}
//         {...props}
//       />
//     );
//   },
// );

// H4.displayName = "H4";

const P = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        className={cn("text-base text-foreground web:select-text", className)}
        ref={ref}
        {...props}
      />
    );
  },
);
P.displayName = "P";

const BlockQuote = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        // @ts-expect-error - role of blockquote renders blockquote element on the web
        role={Platform.OS === "web" ? "blockquote" : undefined}
        className={cn(
          "mt-6 native:mt-4 border-l-2 border-border pl-6 native:pl-3 text-base text-foreground italic web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

BlockQuote.displayName = "BlockQuote";

const Code = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        // @ts-expect-error - role of code renders code element on the web
        role={Platform.OS === "web" ? "code" : undefined}
        className={cn(
          "relative rounded-md bg-muted px-[0.3rem] py-[0.2rem] text-sm text-foreground font-semibold web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

Code.displayName = "Code";

const Lead = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        className={cn(
          "text-xl text-muted-foreground web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

Lead.displayName = "Lead";

const Large = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        className={cn(
          "text-xl text-foreground font-semibold web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

Large.displayName = "Large";

const Small = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        className={cn(
          "text-sm text-foreground font-medium leading-none web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

Small.displayName = "Small";

const Muted = React.forwardRef<TextRef, SlottableTextProps>(
  ({ className, asChild = false, ...props }, ref) => {
    const Component = asChild ? Slot.Text : RNText;
    return (
      <Component
        className={cn(
          "text-sm text-muted-foreground web:select-text",
          className,
        )}
        ref={ref}
        {...props}
      />
    );
  },
);

Muted.displayName = "Muted";

export { BlockQuote, Code, H1, H2, H3, Large, Lead, Muted, P, Small };
