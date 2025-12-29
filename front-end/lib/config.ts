// Environment configuration
// All values should come from environment variables
// Fallback values are for development only

export const config = {
  api: {
    baseUrl:
      process.env.NEXT_PUBLIC_API_URL ||
      (process.env.NODE_ENV === "production"
        ? ""
        : "http://localhost:8080/api"),
    timeout: Number(process.env.NEXT_PUBLIC_API_TIMEOUT) || 30000,
  },
  app: {
    name: process.env.NEXT_PUBLIC_APP_NAME || "Sport Shop",
    url:
      process.env.NEXT_PUBLIC_SITE_URL ||
      (process.env.NODE_ENV === "production" ? "" : "http://localhost:3000"),
  },
  features: {
    enableCart: process.env.NEXT_PUBLIC_ENABLE_CART !== "false",
    enableCheckout: process.env.NEXT_PUBLIC_ENABLE_CHECKOUT !== "false",
    enableSearch: process.env.NEXT_PUBLIC_ENABLE_SEARCH !== "false",
  },
} as const;

// Validate required environment variables in production
if (process.env.NODE_ENV === "production") {
  if (!config.api.baseUrl) {
    throw new Error(
      "NEXT_PUBLIC_API_URL is required in production. Please set it in your environment variables."
    );
  }
  if (!config.app.url) {
    throw new Error(
      "NEXT_PUBLIC_SITE_URL is required in production. Please set it in your environment variables."
    );
  }
}
