import apiClient from "@/lib/http/client";
import { ApiResponse } from "@/types/api";

export interface CheckoutRequest {
  items: Array<{
    productId: string;
    quantity: number;
  }>;
  shippingAddress: {
    fullName: string;
    address: string;
    city: string;
    postalCode: string;
    country: string;
  };
  paymentMethod: string;
}

export interface Order {
  id: string;
  orderNumber: string;
  status: string;
  total: number;
  createdAt: string;
}

export const checkoutApi = {
  // Create order
  createOrder: async (data: CheckoutRequest): Promise<ApiResponse<Order>> => {
    const response = await apiClient.post<ApiResponse<Order>>("/orders", data);
    return response.data;
  },

  // Get order by ID
  getOrder: async (id: string): Promise<ApiResponse<Order>> => {
    const response = await apiClient.get<ApiResponse<Order>>(`/orders/${id}`);
    return response.data;
  },
};

