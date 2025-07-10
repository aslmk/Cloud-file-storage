package com.aslmk.cloudfilestorage.service.Impl;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
import com.aslmk.cloudfilestorage.util.StoragePathHelperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryListingServiceImpl implements DirectoryListingService {

    private final StoragePathHelperUtil storagePathHelperUtil;

    public DirectoryListingServiceImpl(StoragePathHelperUtil storagePathHelperUtil) {
        this.storagePathHelperUtil = storagePathHelperUtil;
    }

    @Override
    public List<S3ItemInfoDto> listItems(String path) {
        List<S3Path> userItems = storagePathHelperUtil.getItemsAbsolutePath(path, false);
        return userItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private S3ItemInfoDto toDto(S3Path item) {
        return S3ItemInfoDto.builder()
                .itemName(item.getItemName())
                .absolutePath(removeUserRootFolder(item.absolutePath()))
                .isDirectory(item.isDirectory())
                .parentPath(item.getParentPath())
                .build();
    }
    private String removeUserRootFolder(String fullPath) {
        return fullPath.substring(fullPath.indexOf("/")+1);
    }
}
