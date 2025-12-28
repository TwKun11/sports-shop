package org.kun.backend.security;

public enum PermissionEnum {
    // USER permissions
    USER_READ("user:read", "Read user data"),
    USER_WRITE("user:write", "Modify user data"),
    USER_DELETE("user:delete", "Delete user"),

    // PRODUCT permissions
    PRODUCT_READ("product:read", "Read products"),
    PRODUCT_CREATE("product:create", "Create products"),
    PRODUCT_UPDATE("product:update", "Update products"),
    PRODUCT_DELETE("product:delete", "Delete products"),

    // ORDER permissions
    ORDER_READ("order:read", "Read orders"),
    ORDER_CREATE("order:create", "Create orders"),
    ORDER_UPDATE("order:update", "Update orders"),
    ORDER_DELETE("order:delete", "Delete orders"),

    // ADMIN permissions
    ADMIN_READ("admin:read", "Admin read access"),
    ADMIN_WRITE("admin:write", "Admin write access"),
    ADMIN_DELETE("admin:delete", "Admin delete access"),
    ADMIN_MANAGE_USERS("admin:manage_users", "Manage users"),
    ADMIN_MANAGE_ROLES("admin:manage_roles", "Manage roles");

    private final String permission;
    private final String description;

    PermissionEnum(String permission, String description) {
        this.permission = permission;
        this.description = description;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }
}
