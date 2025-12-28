"use client";

import { useAuth } from "@/features/auth";
import Link from "next/link";

export default function AccountPage() {
  const { user } = useAuth();

  return (
    <div className="rounded-lg border-4 border-dashed border-gray-200 p-8">
      <h2 className="text-2xl font-bold text-gray-900 mb-4">
        Welcome, {user?.username}!
      </h2>
      <p className="text-gray-600 mb-4">
        Manage your account settings and preferences.
      </p>
      <div className="mt-6 space-x-4">
        <Link
          href="/account/orders"
          className="text-blue-600 hover:text-blue-500 font-medium"
        >
          View Orders →
        </Link>
        <Link
          href="/account/profile"
          className="text-blue-600 hover:text-blue-500 font-medium"
        >
          Edit Profile →
        </Link>
      </div>
    </div>
  );
}
