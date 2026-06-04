/**
 * React-19-safe replacement for react-native-gesture-handler's
 * `GestureDetector/Wrap.web.tsx` (wired up via `withGestureHandlerReact19Fix`
 * in metro.config.js, web only).
 *
 * The upstream implementation (still present in v2.29.1) calls
 * `isRNSVGNode(child)`, which reads `node.ref` — React 19 moved element refs
 * to `props.ref` and logs "Accessing element.ref was removed in React 19" on
 * every such access. This copy is identical except the `rngh` marker is read
 * from `child.props.ref` instead.
 *
 * Remove once upstream fixes the `node.ref` access in `src/web/utils.ts`.
 */

import type { LegacyRef, PropsWithChildren } from "react";
import React, { forwardRef } from "react";
import { tagMessage } from "react-native-gesture-handler/src/utils";
import { RNSVGElements } from "react-native-gesture-handler/src/web/utils";

// React-19-safe version of `isRNSVGNode` from
// react-native-gesture-handler/src/web/utils.ts.
// biome-ignore lint/suspicious/noExplicitAny: mirrors the upstream signature
function isRNSVGNode(node: any) {
  // If `ref` has `rngh` field, it means that component comes from Gesture
  // Handler. This is a special case for `Text`, which is present in
  // `RNSVGElements`, yet we don't want to treat it as SVG.
  // React 19 stores element refs on `props.ref` (reading `node.ref` warns).
  if (node?.props?.ref?.rngh) {
    return false;
  }

  return (
    Object.getPrototypeOf(node?.type)?.name === "WebShape" ||
    RNSVGElements.has(node?.type?.displayName)
  );
}

// biome-ignore lint/complexity/noBannedTypes: mirrors the upstream signature
export const Wrap = forwardRef<HTMLDivElement, PropsWithChildren<{}>>(
  ({ children }, ref) => {
    try {
      // biome-ignore lint/suspicious/noExplicitAny: mirrors the upstream code
      const child: any = React.Children.only(children);

      if (isRNSVGNode(child)) {
        return React.cloneElement(child, { ref }, child.props.children);
      }

      return (
        <div
          ref={ref as LegacyRef<HTMLDivElement>}
          style={{ display: "contents" }}
        >
          {child}
        </div>
      );
    } catch (_e) {
      throw new Error(
        tagMessage(
          `GestureDetector got more than one view as a child. If you want the gesture to work on multiple views, wrap them with a common parent and attach the gesture to that view.`,
        ),
      );
    }
  },
);

// On web we never take a path with Reanimated,
// therefore we can simply export Wrap
export const AnimatedWrap = Wrap;
