import { useCallback, useState } from "react";

/**
 * Hook to manage the state of a disclosure component like a modal or a dropdown.
 * @returns A tuple with the state and the actions to open, close or toggle the disclosure.
 * @see https://mantine.dev/hooks/use-disclosure/
 */
export function useDisclosure(initialState = false) {
  const [isOpen, setIsOpen] = useState(initialState);

  const open = useCallback(() => {
    setIsOpen((isOpened) => {
      if (!isOpened) {
        return true;
      }
      return isOpened;
    });
  }, []);

  const close = useCallback(() => {
    setIsOpen((isOpened) => {
      if (isOpened) {
        return false;
      }
      return isOpened;
    });
  }, []);

  const toggle = useCallback(() => {
    isOpen ? close() : open();
  }, [close, open, isOpen]);

  return [isOpen, { open, close, toggle }] as const;
}
