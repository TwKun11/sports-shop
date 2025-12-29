import apiClient from "@/lib/http/client";
import { ApiResponse } from "@/types/api";

export interface Product {
  id: string;
  name: string;
  slug: string;
  price: number;
  description?: string;
  images?: string[];
  category?: string;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
}

export const catalogApi = {
  // Get all products
  getProducts: async (params?: {
    category?: string;
    search?: string;
    page?: number;
    limit?: number;
  }): Promise<ApiResponse<Product[]>> => {
    const response = await apiClient.get<ApiResponse<Product[]>>("/products", {
      params,
    });
    return response.data;
  },

  // Get product by slug
  getProductBySlug: async (slug: string): Promise<ApiResponse<Product>> => {
    const response = await apiClient.get<ApiResponse<Product>>(
      `/products/${slug}`
    );
    return response.data;
  },

  // Get all categories
  getCategories: async (): Promise<ApiResponse<Category[]>> => {
    const response = await apiClient.get<ApiResponse<Category[]>>(
      "/categories"
    );
    return response.data;
  },

  // Get category by slug
  getCategoryBySlug: async (slug: string): Promise<ApiResponse<Category>> => {
    const response = await apiClient.get<ApiResponse<Category>>(
      `/categories/${slug}`
    );
    return response.data;
  },

  // Search products
  searchProducts: async (query: string): Promise<ApiResponse<Product[]>> => {
    const response = await apiClient.get<ApiResponse<Product[]>>(
      "/products/search",
      { params: { q: query } }
    );
    return response.data;
  },
};

