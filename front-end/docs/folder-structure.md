# Frontend Folder Structure - E-commerce Standard

## 📁 Cấu trúc tổng quan

```
front-end/
├── app/                          # Next.js App Router
│   ├── (store)/                 # Storefront routes
│   │   ├── layout.tsx           # Header/Footer chung
│   │   ├── page.tsx             # / (home)
│   │   ├── categories/[slug]/   # /categories/:slug
│   │   ├── products/            # /products, /products/:slug
│   │   ├── search/              # /search?q=
│   │   ├── cart/                # /cart
│   │   ├── checkout/            # /checkout, /checkout/success
│   │   └── ...
│   │
│   ├── (auth)/                  # Authentication routes
│   │   ├── login/               # /login
│   │   └── register/            # /register
│   │
│   ├── (account)/               # Account routes (protected)
│   │   ├── layout.tsx          # Account layout
│   │   ├── account/            # /account, /account/orders, /account/profile
│   │   └── ...
│   │
│   ├── layout.tsx              # Root layout
│   ├── not-found.tsx           # 404 page
│   ├── error.tsx               # Error boundary
│   ├── loading.tsx             # Loading state
│   ├── sitemap.ts              # SEO sitemap
│   └── robots.ts               # SEO robots.txt
│
├── components/                  # React components
│   ├── ui/                     # Reusable UI (Button, Input, Modal...)
│   ├── layout/                 # Header, Footer, Nav
│   ├── product/                # ProductCard, ProductGallery...
│   └── cart/                   # CartItem, CartDrawer...
│
├── features/                    # Feature-based business logic (ALL business code)
│   ├── auth/
│   │   ├── AuthContext.tsx    # Auth context & provider
│   │   ├── authApi.ts         # Auth API calls
│   │   ├── types.ts           # Auth types
│   │   ├── utils.ts           # Auth utilities
│   │   ├── guards.ts          # Role guards & helpers
│   │   └── index.ts           # Public exports
│   ├── catalog/
│   │   ├── api.ts              # Products/categories/search API
│   │   ├── types.ts
│   │   └── utils.ts
│   ├── cart/
│   │   ├── store.ts            # Zustand store
│   │   ├── types.ts
│   │   └── utils.ts
│   ├── checkout/
│   │   ├── api.ts
│   │   └── types.ts
│   └── order/
│       ├── api.ts
│       └── types.ts
│
├── lib/                        # Core infrastructure (NO business logic)
│   ├── http/
│   │   ├── client.ts          # Axios instance + interceptors
│   │   └── errors.ts          # Error handling utilities
│   ├── config.ts              # Environment configuration
│   └── utils.ts               # General utilities (formatDate, slugify...)
│
├── hooks/                      # Custom React hooks
│   └── index.ts               # Re-export hooks
│
├── styles/                     # Global styles
│   └── globals.css
│
├── types/                      # Shared TypeScript types
│   └── api.ts                 # API request/response types
│
├── middleware.ts              # Route protection
├── next.config.ts
├── package.json
└── ...
```

## 🎯 Nguyên tắc tổ chức

### 1. **App Router (`app/`)**

- Route groups `(groupName)` để tổ chức layout, không tạo URL segments
- Server Components mặc định cho SEO
- Client Components chỉ khi cần interaction

### 2. **Components (`components/`)**

- **`ui/`**: Reusable UI components
- **`layout/`**: Layout components (Header, Footer)
- **`product/`**: Product-related components
- **`cart/`**: Cart-related components
- Tổ chức theo domain, không theo feature

### 3. **Features (`features/`)** - All Business Logic

**Mục đích**: Chứa TẤT CẢ business logic, API calls, state management

- Tổ chức theo business domain
- Mỗi feature có:
  - `api.ts` hoặc `authApi.ts` - API calls
  - `types.ts` - Feature types
  - `utils.ts` - Feature-specific utilities
  - `store.ts` - State management (Zustand/Redux)
  - `AuthContext.tsx` - React contexts (nếu cần)
  - `guards.ts` - Authorization guards (nếu cần)
  - `index.ts` - Public exports

### 4. **Lib (`lib/`)**

- Core utilities và shared logic
- **`http/`**: HTTP client configuration
- **`auth/`**: Authentication logic
- **`config.ts`**: Environment config
- **`utils.ts`**: General utilities

### 5. **Hooks (`hooks/`)**

- Custom React hooks
- Re-export từ lib/features

### 6. **Styles (`styles/`)**

- Global CSS files
- Tailwind config

## 📝 Import Paths

```typescript
// ✅ Features - All business logic
import { useAuth, AuthProvider, authApi } from "@/features/auth";
import { catalogApi } from "@/features/catalog/api";
import { useCartStore } from "@/features/cart/store";

// ✅ Lib - Only infrastructure
import { config } from "@/lib/config";
import { formatCurrency, formatDate } from "@/lib/utils";
import apiClient from "@/lib/http/client";

// ✅ Components
import Button from "@/components/ui/Button";
import ProductCard from "@/components/product/ProductCard";

// ✅ Hooks
import { useAuth } from "@/hooks";
```

## 🚀 Best Practices

1. **Lib = Infrastructure Only**:

   - ✅ HTTP client, config, general utils
   - ❌ NO business logic, NO API calls, NO feature code

2. **Features = All Business Logic**:

   - ✅ API calls, state management, contexts, guards
   - ✅ Feature-specific types, utils
   - ✅ Export từ `index.ts`

3. **Middleware for Protection**: Route protection ở middleware

4. **Domain Components**: Components tổ chức theo domain

5. **Type Safety**: Export types từ modules
