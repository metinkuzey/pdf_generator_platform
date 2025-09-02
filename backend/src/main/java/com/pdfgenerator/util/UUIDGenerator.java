package com.pdfgenerator.util;

import java.util.UUID;

/**
 * Utility class for generating UUIDs
 */
public class UUIDGenerator {

    private UUIDGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generate a random UUID string
     * @return UUID string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a random UUID
     * @return UUID object
     */
    public static UUID generateUUIDObject() {
        return UUID.randomUUID();
    }

    /**
     * Validate UUID string format
     * @param uuid UUID string to validate
     * @return true if valid UUID format, false otherwise
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Convert string to UUID with validation
     * @param uuid UUID string
     * @return UUID object
     * @throws IllegalArgumentException if invalid UUID format
     */
    public static UUID fromString(String uuid) {
        if (!isValidUUID(uuid)) {
            throw new IllegalArgumentException("Invalid UUID format: " + uuid);
        }
        return UUID.fromString(uuid);
    }
}