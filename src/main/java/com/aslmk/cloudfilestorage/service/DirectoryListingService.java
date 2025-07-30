package com.aslmk.cloudfilestorage.service;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.TargetFolderDto;
import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;

import java.util.List;

public interface DirectoryListingService {
    List<S3ItemInfoDto> listItems(String path);
    List<TargetFolderDto> listFolders(String currentDirectory);
    List<S3Path> listS3Paths(String folder, boolean recursively);
}
