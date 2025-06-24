package com.aslmk.cloudfilestorage.util;

import org.apache.coyote.BadRequestException;

public final class StorageInputValidator {

    public static void validateItemName(String newItemName) throws BadRequestException {
        if (newItemName == null || newItemName.isBlank() || newItemName.contains("/")) {
            throw new BadRequestException("Invalid file or folder name");
        }
    }

    public static boolean isSearchQueryValid(String query) {
        if (query == null) return false;
        String normalized = query.replaceAll("/+$", "").trim();
        return !normalized.isEmpty();
    }
}
