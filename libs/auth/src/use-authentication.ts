import { useContext } from "react";
import { AuthenticationContext } from "./authentication-context";

/**
 * Hook to get the current user's authentication context.
 */
export function useAuthentication() {
  const context = useContext(AuthenticationContext);

  return context;
}

/**
 * Hook to get the current user's authentication context. By default, this hook
 * will throw an error if the user is not authenticated, so you get a type-safe
 * way to ensure that the user is logged in.
 */
export function useLoggedInAuth() {
  const context = useAuthentication();

  if (!context.isAuthenticated) {
    throw new Error("User is not authenticated");
  }

  return context;
}
