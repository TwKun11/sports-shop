# Frontend architecture — Nguyên tắc vàng

Mục tiêu: dễ maintain, rõ ràng phân vùng, tối ưu SEO.

Key principles:

- Route groups: chia khu vực theo **store / auth / account / admin** để dễ điều hướng và phân quyền.
- Server Components cho các trang SEO-critical (home, listings, PDP, category).
- Client Components chỉ cho interaction (cart drawer, quantity selector, filters, login forms).
- features/ gom logic theo nghiệp vụ: `features/catalog`, `features/cart`, `features/checkout`, `features/order`.
- `lib/http/client.ts` là **một chỗ duy nhất** xử lý fetch / token / error / refresh.

Quick map:

- `/store` — store landing (server)
- `/auth/login`, `/auth/register` — client auth pages
- `/account`, `/account/dashboard` — protected pages

Mẹo:

- Keep API wrappers inside `features/*/api.ts` so business logic stays colocated with UI.
- Small server components should fetch data and pass plain props to child client components.
