package com.aslmk.cloudfilestorage.service;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;

import java.util.List;

public interface DirectoryListingService {
    List<S3ItemInfoDto> listItems(String path);
}
