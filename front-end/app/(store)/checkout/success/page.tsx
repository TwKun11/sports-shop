"use client";

import Link from "next/link";
import Button from "@/components/ui/Button";

export default function CheckoutSuccessPage() {
  return (
    <div className="px-4 py-6 sm:px-0">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">
          Order Placed Successfully!
        </h1>
        <p className="text-gray-600 mb-6">
          Thank you for your purchase. Your order has been confirmed.
        </p>
        <Link href="/account/orders">
          <Button>View Orders</Button>
        </Link>
      </div>
    </div>
  );
}
