"use client";

import AccountNav from "@/components/account/AccountNav";

export default function AccountLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  // Middleware đã handle protection, không cần ProtectedRoute nữa
  return (
    <div className="min-h-screen bg-gray-50">
      <AccountNav />
      <main className="mx-auto max-w-7xl py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">{children}</div>
      </main>
    </div>
  );
}

