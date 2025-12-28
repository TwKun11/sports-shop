# Documentation

## 📚 Files

- **`architecture.md`** - Nguyên tắc và best practices cho frontend architecture
- **`folder-structure.md`** - Chi tiết cấu trúc folder và naming conventions

## 🎯 Quick Reference

### Import Paths

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
import Header from "@/components/layout/Header";
import Button from "@/components/ui/Button";
import ProductCard from "@/components/product/ProductCard";
```

### Folder Structure

```
app/              # Next.js routes
components/       # React components
  ├── layout/    # Layout components
  ├── ui/         # Reusable UI
  ├── product/    # Product components
  └── cart/       # Cart components
lib/              # Core infrastructure (NO business logic)
  └── http/       # HTTP client, errors
features/         # ALL business logic
  ├── auth/       # Authentication (context, API, guards)
  ├── catalog/    # Products, categories
  ├── cart/       # Cart store
  └── ...
hooks/            # Custom hooks
styles/           # Global styles
types/            # Shared types
```

## 📝 Key Principles

### Lib vs Features

- **`lib/`**: Chỉ infrastructure code

  - HTTP client setup
  - Config
  - General utilities (formatDate, slugify...)
  - ❌ NO business logic

- **`features/`**: Tất cả business logic
  - API calls
  - State management
  - React contexts
  - Feature-specific types & utils
  - ✅ ALL business code ở đây
