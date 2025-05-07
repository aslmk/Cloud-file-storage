package com.aslmk.cloudfilestorage.util;

import org.apache.coyote.BadRequestException;

public final class StorageInputValidator {

    public static void validateItemName(String newItemName) throws BadRequestException {
        if (newItemName == null || newItemName.isBlank() || newItemName.contains("/")) {
            throw new BadRequestException("Invalid file or folder name");
        }
    }

    public static boolean isSearchQueryValid(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        return !query.replaceAll("/+$", "").isEmpty();
    }
}
