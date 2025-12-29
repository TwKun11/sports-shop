"use client";

interface ProductGalleryProps {
  images: string[];
}

export default function ProductGallery({ images }: ProductGalleryProps) {
  return (
    <div className="grid grid-cols-1 gap-4">
      {images.map((image, index) => (
        <div key={index} className="aspect-square bg-gray-200 rounded-lg"></div>
      ))}
    </div>
  );
}

