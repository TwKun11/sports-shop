import { Product } from "./types";

// Catalog utility functions

export function formatPrice(price: number): string {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(price);
}

export function getProductImageUrl(
  product: Product,
  index: number = 0
): string | undefined {
  return product.images?.[index];
}

export function getProductSlug(product: Product): string {
  return product.slug || product.id;
}

