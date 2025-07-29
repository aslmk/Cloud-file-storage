package com.aslmk.cloudfilestorage.service;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.S3Path;

import java.util.List;

public interface DirectoryListingService {
    List<S3ItemInfoDto> listItems(String path);
    List<S3Path> getItemsAbsolutePath(String folder, boolean recursively);
}
