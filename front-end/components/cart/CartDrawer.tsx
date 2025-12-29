"use client";

import Link from "next/link";
import { useMemo } from "react";
import CartItem from "./CartItem";
import { useCartStore } from "@/features/cart/store";
import Button from "@/components/ui/Button";

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function CartDrawer({ isOpen, onClose }: CartDrawerProps) {
  const items = useCartStore((s) => s.items);
  const removeItem = useCartStore((s) => s.removeItem);
  const updateQuantity = useCartStore((s) => s.updateQuantity);
  const clearCart = useCartStore((s) => s.clearCart);

  const itemCount = useMemo(
    () => items.reduce((c, i) => c + i.quantity, 0),
    [items]
  );

  const subtotal = useMemo(
    () => items.reduce((total, item) => total + item.price * item.quantity, 0),
    [items]
  );

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50">
      <div
        className="absolute inset-0 bg-black bg-opacity-50"
        onClick={onClose}
      />
      <div className="absolute right-0 top-0 h-full w-96 bg-white shadow-xl flex flex-col">
        <div className="p-4 flex items-center justify-between border-b">
          <h2 className="text-xl font-bold">Shopping Cart</h2>
          <button
            onClick={onClose}
            className="text-gray-500"
            aria-label="Close cart"
          >
            ✕
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-4 space-y-2">
          {items.length === 0 ? (
            <p className="text-gray-600 text-center py-8">Your cart is empty</p>
          ) : (
            items.map((it) => (
              <div key={it.id} className="border rounded">
                <div className="p-2">
                  <CartItem
                    id={it.id}
                    name={it.name}
                    price={it.price}
                    quantity={it.quantity}
                    image={it.image}
                  />
                  <div className="flex items-center justify-between p-2">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() =>
                          updateQuantity(it.id, Math.max(1, it.quantity - 1))
                        }
                        className="px-2 py-1 border rounded"
                        aria-label="Decrease quantity"
                      >
                        −
                      </button>
                      <span className="px-2">{it.quantity}</span>
                      <button
                        onClick={() => updateQuantity(it.id, it.quantity + 1)}
                        className="px-2 py-1 border rounded"
                        aria-label="Increase quantity"
                      >
                        +
                      </button>
                    </div>
                    <button
                      onClick={() => removeItem(it.id)}
                      className="text-sm text-red-600"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        <div className="p-4 border-t">
          <div className="flex justify-between mb-2">
            <div className="text-sm text-gray-600">Items</div>
            <div className="font-semibold">{itemCount}</div>
          </div>
          <div className="flex justify-between mb-4">
            <div className="text-sm text-gray-600">Subtotal</div>
            <div className="font-semibold">${subtotal.toFixed(2)}</div>
          </div>

          <div className="space-y-2">
            <Link href="/checkout">
              <Button className="w-full">Proceed to Checkout</Button>
            </Link>
            <Button
              variant="secondary"
              className="w-full"
              onClick={() => clearCart()}
            >
              Clear Cart
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

