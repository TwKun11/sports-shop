"use client";

import React, { useState } from "react";
import { useAuth } from "@/features/auth";
import Link from "next/link";
import CartDrawer from "@/components/cart/CartDrawer";
import { useCartStore } from "@/features/cart/store";

export default function Header() {
  const { user, logout, isLoading } = useAuth();
  const [isCartOpen, setIsCartOpen] = useState(false);

  // subscribe to item count
  const itemCount = useCartStore((s) =>
    s.items.reduce((c, i) => c + i.quantity, 0)
  );

  return (
    <nav className="bg-white shadow">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 justify-between items-center">
          <div className="flex items-center gap-4">
            <Link href="/" className="text-xl font-bold text-gray-900">
              Sport Shop
            </Link>
            <button
              onClick={() => setIsCartOpen(true)}
              aria-label="Open cart"
              className="relative inline-flex items-center px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-100"
            >
              🛒
              {itemCount > 0 && (
                <span className="ml-2 inline-flex items-center justify-center rounded-full bg-red-600 px-2 py-0.5 text-xs font-semibold text-white">
                  {itemCount}
                </span>
              )}
            </button>
          </div>
          <div className="flex items-center">
            {isLoading ? (
              <div className="h-6 w-20 bg-gray-100 animate-pulse rounded" />
            ) : user ? (
              <>
                <span className="text-sm text-gray-700 mr-4">
                  Welcome, <strong>{user.username}</strong>
                </span>
                <button
                  onClick={() => logout()}
                  className="rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold text-white hover:bg-blue-500 transition-colors"
                >
                  Logout
                </button>
              </>
            ) : (
              <Link
                href="/login"
                className="text-sm text-blue-600 hover:text-blue-500"
              >
                Sign in
              </Link>
            )}
          </div>
        </div>
      </div>

      <CartDrawer isOpen={isCartOpen} onClose={() => setIsCartOpen(false)} />
    </nav>
  );
}
