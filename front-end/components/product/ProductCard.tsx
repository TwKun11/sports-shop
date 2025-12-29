"use client";

import Link from "next/link";

interface ProductCardProps {
  id: string;
  name: string;
  price: number;
  image?: string;
  slug: string;
}

export default function ProductCard({
  id,
  name,
  price,
  image,
  slug,
}: ProductCardProps) {
  return (
    <Link href={`/products/${slug}`}>
      <div className="rounded-lg border border-gray-200 p-4 hover:shadow-lg transition-shadow">
        {image && (
          <div className="aspect-square bg-gray-200 rounded mb-4"></div>
        )}
        <h3 className="font-semibold text-gray-900 mb-2">{name}</h3>
        <p className="text-lg font-bold text-blue-600">${price}</p>
      </div>
    </Link>
  );
}

