import apiClient from "@/lib/http/client";
import {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from "@/types/api";

export const authApi = {
  // Đăng ký
  register: async (
    data: RegisterRequest
  ): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(
      "/auth/register",
      data
    );
    return response.data;
  },

  // Đăng nhập
  login: async (data: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(
      "/auth/login",
      data
    );
    return response.data;
  },

  // Refresh token (tự động gọi qua interceptor, nhưng gọi thủ công nên dùng axios trực tiếp
  // to avoid possible request/response interceptor recursion)
  refresh: async (): Promise<ApiResponse<AuthResponse>> => {
    // Use direct axios call with withCredentials to ensure we send the refresh cookie
    const axios = (await import("axios")).default;
    const baseUrl = (await import("@/lib/config")).config.api.baseUrl;
    const response = await axios.post<ApiResponse<AuthResponse>>(
      `${baseUrl}/auth/refresh`,
      {},
      {
        withCredentials: true,
      }
    );
    return response.data;
  },

  // Đăng xuất
  logout: async (): Promise<ApiResponse<null>> => {
    const response = await apiClient.post<ApiResponse<null>>(
      "/auth/logout",
      {}
    );
    return response.data;
  },

  // Revoke tất cả tokens
  revokeAll: async (): Promise<ApiResponse<null>> => {
    const response = await apiClient.post<ApiResponse<null>>(
      "/auth/revoke-all",
      {}
    );
    return response.data;
  },
};
