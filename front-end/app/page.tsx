import type { Metadata } from "next";
import Link from "next/link";
import Header from "@/components/layout/Header";

export const metadata: Metadata = {
  title: "Sport Shop",
  description:
    "Buy quality sports gear online. Best sellers, new arrivals, and fast delivery.",
  alternates: { canonical: "/" },
  openGraph: {
    title: "Sport Shop",
    description:
      "Buy quality sports gear online. Best sellers, new arrivals, and fast delivery.",
    url: "/",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "Sport Shop",
    description:
      "Buy quality sports gear online. Best sellers, new arrivals, and fast delivery.",
  },
};

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      <main className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
        <section className="rounded-2xl border border-gray-200 bg-white p-8 shadow-sm">
          <h1 className="text-3xl font-bold text-gray-900">Sport Shop</h1>

          <p className="mt-3 max-w-2xl text-gray-600">
            Your trusted destination for premium sports gear. Fast delivery,
            easy returns, and top brands.
          </p>
          <div className="mt-6 flex flex-wrap gap-3">
            <Link
              href="/products"
              className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:opacity-90"
            >
              Shop now
            </Link>

            <Link
              href="/categories"
              className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-900 hover:bg-gray-50"
            >
              Categories
            </Link>

            <Link
              href="/login"
              className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-2 text-sm font-medium text-blue-700 hover:bg-blue-100"
            >
              Sign in →
            </Link>
          </div>
        </section>
      </main>
    </div>
  );
}
