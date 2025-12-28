// Server component for product detail page (SEO-friendly)
export default function ProductDetailPage({
  params,
}: {
  params: { slug: string };
}) {
  return (
    <div className="px-4 py-6 sm:px-0">
      <h1 className="text-3xl font-bold text-gray-900 mb-4">
        Product: {params.slug}
      </h1>
      <p className="text-gray-600">Product detail page</p>
    </div>
  );
}
