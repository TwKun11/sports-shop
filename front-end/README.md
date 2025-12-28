# Sport Shop Frontend

Frontend application cho Sport Shop với authentication system và e-commerce features hoàn chỉnh.

## Cấu trúc dự án

```
front-end/
├── app/                   # Next.js App Router
│   ├── (store)/           # Storefront routes (/, /products, /cart, /checkout...)
│   ├── (auth)/            # Auth routes (/login, /register)
│   ├── (account)/         # Account routes (/account, /account/orders...)
│   ├── layout.tsx         # Root layout với AuthProvider
│   ├── not-found.tsx      # 404 page
│   ├── error.tsx          # Error boundary
│   ├── loading.tsx        # Loading state
│   ├── sitemap.ts         # SEO sitemap
│   └── robots.ts          # SEO robots.txt
│
├── components/            # React components
│   ├── ui/                # Reusable UI (Button, Input, Modal...)
│   ├── layout/            # Header, Footer, Nav
│   ├── product/           # ProductCard, ProductGallery...
│   └── cart/              # CartItem, CartDrawer...
│
├── features/              # ALL business logic
│   ├── auth/              # AuthContext, authApi, types, utils, guards
│   ├── catalog/           # Products, categories, search API
│   ├── cart/              # Cart store (Zustand)
│   ├── checkout/          # Checkout API
│   └── order/             # Order API
│
├── lib/                   # Core infrastructure (NO business logic)
│   ├── http/              # HTTP client + error handling
│   ├── config.ts          # Environment config
│   └── utils.ts           # General utilities (formatDate, slugify...)
│
├── hooks/                 # Custom React hooks
├── styles/                # Global styles
├── types/                 # Shared TypeScript types
├── middleware.ts         # Route protection
└── ...
```

## Tính năng

- ✅ Đăng ký/Đăng nhập với JWT
- ✅ Tự động refresh token
- ✅ Protected routes (middleware-based)
- ✅ Shopping cart (Zustand)
- ✅ Product catalog
- ✅ Checkout flow
- ✅ Order management
- ✅ SEO optimized (sitemap, robots.txt)

## Cài đặt

1. Cài đặt dependencies:

```bash
npm install
```

2. Tạo file `.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_SITE_URL=http://localhost:3000
```

3. Chạy development server:

```bash
npm run dev
```

## Sử dụng

### Authentication

```tsx
import { useAuth, AuthProvider } from "@/features/auth";

function MyComponent() {
  const { user, isAuthenticated, login, logout } = useAuth();
  // ...
}
```

### Route Protection

Routes được bảo vệ tự động bởi `middleware.ts`:

- Routes bắt đầu với `/account` yêu cầu authentication
- Nếu chưa login, tự động redirect về `/login`
- Middleware chạy ở edge runtime, hiệu quả hơn component wrappers

### Shopping Cart

```tsx
import { useCartStore } from "@/features/cart/store";

function CartComponent() {
  const { items, addItem, getTotal } = useCartStore();
  // ...
}
```

### API Calls

```tsx
import { catalogApi } from "@/features/catalog/api";

const products = await catalogApi.getProducts();
```

## API Endpoints

- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/refresh` - Refresh token (tự động)
- `POST /api/auth/logout` - Đăng xuất
- `GET /api/products` - Danh sách sản phẩm
- `GET /api/products/:slug` - Chi tiết sản phẩm
- `GET /api/categories` - Danh sách categories
- `POST /api/orders` - Tạo order

## Lưu ý

- Refresh token được lưu trong httpOnly cookie (tự động gửi với mọi request)
- Access token được lưu trong localStorage và tự động refresh khi hết hạn
- Tất cả API calls tự động retry khi access token hết hạn
- Cart state được persist trong localStorage (Zustand)
