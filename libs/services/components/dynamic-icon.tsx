import { icons } from "lucide-react-native";

const camelRegex = /-[a-z]/g;

/**
 * Transforms kebab-case to camelCase: foo-bar -> fooBar
 */
function camelCase(str: string) {
  return str.replace(camelRegex, (match) => match.slice(1).toUpperCase());
}

/**
 * Uppercase the first letter: foo -> Foo
 */
function ucFirst(str: string) {
  return str.slice(0, 1).toUpperCase() + str.slice(1);
}

export function DynamicIcon({
  name,
  color,
  size,
  className,
}: { name: string; color: string; size: number; className?: string }) {
  const LucideIcon = icons[ucFirst(camelCase(name)) as keyof typeof icons];

  return LucideIcon ? (
    <LucideIcon color={color} size={size} className={className} />
  ) : null;
}
