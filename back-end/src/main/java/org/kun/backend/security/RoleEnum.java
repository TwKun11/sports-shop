package org.kun.backend.security;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN("ADMIN", "Administrator"),
    USER("USER", "Regular User"),
    MANAGER("MANAGER", "Manager");

    private final String name;
    private final String description;

    RoleEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
