package com.pdfgenerator.enums;

public enum UserRole {
    ADMIN("Yönetici"),
    USER("Kullanıcı"),
    VIEWER("Görüntüleyici");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}