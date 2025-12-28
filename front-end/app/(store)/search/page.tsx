// Server component for search page (SEO-friendly)
export default function SearchPage({
  searchParams,
}: {
  searchParams: { q?: string };
}) {
  return (
    <div className="px-4 py-6 sm:px-0">
      <h1 className="text-3xl font-bold text-gray-900 mb-4">Search</h1>
      {searchParams.q && (
        <p className="text-gray-600">Search results for: {searchParams.q}</p>
      )}
    </div>
  );
}
