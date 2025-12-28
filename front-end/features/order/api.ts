import apiClient from "@/lib/http/client";
import { ApiResponse } from "@/types/api";
import { Order } from "@/features/checkout/types";

export const orderApi = {
  // Get user orders
  getOrders: async (): Promise<ApiResponse<Order[]>> => {
    const response = await apiClient.get<ApiResponse<Order[]>>("/orders");
    return response.data;
  },

  // Get order by ID
  getOrderById: async (id: string): Promise<ApiResponse<Order>> => {
    const response = await apiClient.get<ApiResponse<Order>>(`/orders/${id}`);
    return response.data;
  },

  // Cancel order
  cancelOrder: async (id: string): Promise<ApiResponse<null>> => {
    const response = await apiClient.post<ApiResponse<null>>(
      `/orders/${id}/cancel`
    );
    return response.data;
  },
};
