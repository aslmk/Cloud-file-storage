package com.aslmk.cloudfilestorage.service.Impl;

import com.aslmk.cloudfilestorage.dto.TargetFolderDto;
import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
import com.aslmk.cloudfilestorage.util.StoragePathHelperUtil;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryListingServiceImpl implements DirectoryListingService {

    @Value("${empty-folder-marker}")
    private String EMPTY_FOLDER;

    private final StoragePathHelperUtil storagePathHelperUtil;

    private final UserPathResolver userPathResolver;

    public DirectoryListingServiceImpl(StoragePathHelperUtil storagePathHelperUtil, UserPathResolver userPathResolver) {
        this.storagePathHelperUtil = storagePathHelperUtil;
        this.userPathResolver = userPathResolver;
    }

    @Override
    public List<S3ItemInfoDto> listItems(String path) {
        List<S3Path> userItems = storagePathHelperUtil.getItemsAbsolutePath(path, false);
        return userItems.stream()
                .map(this::toDto)
                .filter(item -> !item.getAbsolutePath().endsWith(EMPTY_FOLDER))
                .collect(Collectors.toList());
    }

    @Override
    public List<TargetFolderDto> listFolders(String currentDirectory) {
        List<TargetFolderDto> folders = storagePathHelperUtil.getFolders(
                userPathResolver.getUserRootFolder(),
                true);

        List<TargetFolderDto> result = new ArrayList<>();

        String userRootFolder = userPathResolver.getUserRootFolder();

        for (TargetFolderDto folder : folders) {
            String name = folder.getName();
            String displayPath = removeUserRootFolder(folder.getPath());
            String fullPath = folder.getPath();

            if (fullPath.equals(currentDirectory)) {
                continue;
            }

            if ((name + "/").equals(userRootFolder)) {
                folder.setName("Home");
            }

            folder.setPath(displayPath);
            result.add(folder);
        }

        return result;
    }

    private S3ItemInfoDto toDto(S3Path item) {
        return S3ItemInfoDto.builder()
                .itemName(item.getItemName())
                .absolutePath(removeUserRootFolder(item.absolutePath()))
                .isDirectory(item.isDirectory())
                .parentPath(removeUserRootFolder(item.getParentPath()))
                .build();
    }
    private String removeUserRootFolder(String fullPath) {
        return fullPath.substring(fullPath.indexOf("/")+1);
    }
}
