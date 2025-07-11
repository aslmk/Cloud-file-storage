package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.UploadItemRequestDto;

public interface StorageService {
    void saveItem(UploadItemRequestDto item);
    void renameItem(String oldItemName, String newItemName);
    void removeItem(String itemName);
}
