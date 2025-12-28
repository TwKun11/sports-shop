"use client";

import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  useCallback,
} from "react";
import { authApi } from "./authApi";
import { LoginRequest, RegisterRequest } from "@/types/api";
import { setAccessToken, clearAccessToken } from "@/lib/authToken";

// Utility: safe error message extractor
const getErrorMessage = (e: unknown, fallback: string) => {
  if (!e) return fallback;
  if (typeof e === "object" && e !== null) {
    const obj = e as Record<string, unknown>;
    // try common shapes without using `any`
    const resp = obj["response"] as Record<string, unknown> | undefined;
    const data = resp?.["data"] as Record<string, unknown> | undefined;
    const maybeMsg1 = data?.["message"];
    if (typeof maybeMsg1 === "string" && maybeMsg1.length) return maybeMsg1;

    const maybeMsg2 = obj["message"];
    if (typeof maybeMsg2 === "string" && maybeMsg2.length) return maybeMsg2;
  }
  return String(e) || fallback;
};

export interface AuthContextType {
  user: { username: string } | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshToken: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<{ username: string } | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Refresh token: call server /auth/refresh which uses refresh cookie
  const refreshToken = useCallback(async () => {
    try {
      const response = await authApi.refresh();
      if (response.success && response.data) {
        const { accessToken, username } = response.data;
        // Store accessToken in-memory only (mitigates XSS risk)
        setAccessToken(accessToken);
        setUser({ username });
      }
    } catch (err) {
      // Clear in-memory token on failure
      clearAccessToken();
      setUser(null);
      throw err;
    }
  }, []);

  // On mount, try to refresh token to validate and fetch username
  useEffect(() => {
    const initAuth = async () => {
      try {
        await refreshToken();
      } catch {
        // refreshToken already clears token on failure
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    initAuth();
  }, [refreshToken]);

  // Đăng nhập
  const login = useCallback(async (credentials: LoginRequest) => {
    try {
      const response = await authApi.login(credentials);

      if (response.success && response.data) {
        const { accessToken, username } = response.data;
        // store access token in-memory only
        setAccessToken(accessToken);
        setUser({ username });
      } else {
        throw new Error(response.message || "Login failed");
      }
    } catch (error: unknown) {
      const errorMessage = getErrorMessage(error, "Login failed");
      throw new Error(errorMessage);
    }
  }, []);

  // Đăng ký
  const register = useCallback(async (data: RegisterRequest) => {
    try {
      const response = await authApi.register(data);

      if (response.success && response.data) {
        const { accessToken, username } = response.data;
        setAccessToken(accessToken);
        setUser({ username });
      } else {
        throw new Error(response.message || "Registration failed");
      }
    } catch (error: unknown) {
      const errorMessage = getErrorMessage(error, "Registration failed");
      throw new Error(errorMessage);
    }
  }, []);

  // Đăng xuất
  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch (error) {
      console.error("Logout error:", error);
    } finally {
      clearAccessToken();
      setUser(null);
    }
  }, []);

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isLoading,
    login,
    register,
    logout,
    refreshToken,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
