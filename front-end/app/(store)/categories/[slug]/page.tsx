// Server component for category page (SEO-friendly)
export default function CategoryPage({ params }: { params: { slug: string } }) {
  return (
    <div className="px-4 py-6 sm:px-0">
      <h1 className="text-3xl font-bold text-gray-900 mb-4">
        Category: {params.slug}
      </h1>
      <p className="text-gray-600">Category page</p>
    </div>
  );
}

