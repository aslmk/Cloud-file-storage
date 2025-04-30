package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.dto.UploadItemRequestDto;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface StorageService {
    void saveItem(UploadItemRequestDto item) throws BadRequestException;
    void renameItem(String oldItemName, String newItemName);
    void removeItem(String itemName);
    List<SearchResultsDto> searchItem(String S3UserItemsPath, String query);
    List<S3ItemInfoDto> getAllItems(String S3UserItemsPath);
}
