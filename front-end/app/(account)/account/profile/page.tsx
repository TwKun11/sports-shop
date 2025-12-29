"use client";

import { useAuth } from "@/features/auth";

export default function ProfilePage() {
  const { user } = useAuth();

  return (
    <div className="rounded-lg bg-white shadow p-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-4">Profile</h1>
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Username
          </label>
          <p className="mt-1 text-gray-900">{user?.username}</p>
        </div>
        <p className="text-gray-600">Profile editing form will go here.</p>
      </div>
    </div>
  );
}

