import { AxiosError } from "axios";
import { ApiResponse } from "@/types/api";

export class ApiError extends Error {
  constructor(
    message: string,
    public statusCode?: number,
    public data?: unknown
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export function handleApiError(error: unknown): string {
  if (error instanceof ApiError) {
    return error.message;
  }

  if (error instanceof AxiosError) {
    const apiResponse = error.response?.data as
      | ApiResponse<unknown>
      | undefined;
    if (apiResponse?.message) {
      return apiResponse.message;
    }
    return error.message || "An error occurred";
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "An unexpected error occurred";
}

export function isApiError(error: unknown): error is ApiError {
  return error instanceof ApiError;
}

