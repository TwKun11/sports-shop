// Auth helper functions and role guards

import { useAuth } from "./AuthContext";

export function useRequireAuth() {
  const { isAuthenticated, isLoading } = useAuth();

  if (!isLoading && !isAuthenticated) {
    throw new Error("Authentication required");
  }

  return { isAuthenticated, isLoading };
}

export function useRequireRole(role: string) {
  const { user, isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    throw new Error("Authentication required");
  }

  return { user, isAuthenticated };
}

export function hasRole(user: { role?: string } | null, role: string): boolean {
  return user?.role === role;
}

export function isAdmin(user: { role?: string } | null): boolean {
  return hasRole(user, "admin");
}

