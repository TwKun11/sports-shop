// Environment configuration

export const config = {
  api: {
    baseUrl: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api",
    timeout: 30000,
  },
  app: {
    name: "Sport Shop",
    url: process.env.NEXT_PUBLIC_SITE_URL || "http://localhost:3000",
  },
  features: {
    enableCart: true,
    enableCheckout: true,
    enableSearch: true,
  },
} as const;
