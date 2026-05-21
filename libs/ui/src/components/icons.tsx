import {
  Check,
  ChevronDown,
  ChevronUp,
  type LucideIcon,
  X,
} from "lucide-react-native";
import { cssInterop } from "nativewind";

function interopIcon(icon: LucideIcon) {
  cssInterop(icon, {
    className: {
      target: "style",
      nativeStyleToProp: {
        color: true,
        opacity: true,
      },
    },
  });
}

interopIcon(Check);
interopIcon(ChevronDown);
interopIcon(ChevronUp);
interopIcon(X);

export { Check, ChevronDown, ChevronUp, X };
