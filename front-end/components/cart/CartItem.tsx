"use client";

interface CartItemProps {
  id: string;
  name: string;
  price: number;
  quantity: number;
  image?: string;
}

export default function CartItem({
  id,
  name,
  price,
  quantity,
  image,
}: CartItemProps) {
  return (
    <div className="flex items-center gap-4 p-4 border-b border-gray-200">
      {image && <div className="w-20 h-20 bg-gray-200 rounded"></div>}
      <div className="flex-1">
        <h3 className="font-semibold text-gray-900">{name}</h3>
        <p className="text-gray-600">
          ${price} x {quantity}
        </p>
      </div>
      <p className="text-lg font-bold text-gray-900">
        ${(price * quantity).toFixed(2)}
      </p>
    </div>
  );
}
