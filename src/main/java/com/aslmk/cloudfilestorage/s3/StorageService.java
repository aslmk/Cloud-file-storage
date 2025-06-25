package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.UploadItemRequestDto;
import org.apache.coyote.BadRequestException;

public interface StorageService {
    void saveItem(UploadItemRequestDto item) throws BadRequestException;
    void renameItem(String oldItemName, String newItemName);
    void removeItem(String itemName);
}
