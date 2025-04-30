package com.aslmk.cloudfilestorage.util;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class StorageItemValidator {

    public void isNewItemNameCorrect(String newItemName) throws BadRequestException {
        if (newItemName == null || newItemName.isBlank() || newItemName.contains("/")) {
            throw new BadRequestException("Invalid file or folder name");
        }
    }
}
