package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.*;
import com.aslmk.cloudfilestorage.exception.StorageException;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.util.StoragePathHelperUtil;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MinioServiceImpl implements StorageService {

    private final StoragePathHelperUtil storagePathHelperUtil;
    private final MinioRepository minioRepository;
    private final UserPathResolver userPathResolver;

    public MinioServiceImpl(StoragePathHelperUtil storagePathHelperUtil, MinioRepository minioRepository, UserPathResolver userPathResolver) {
        this.storagePathHelperUtil = storagePathHelperUtil;
        this.minioRepository = minioRepository;
        this.userPathResolver = userPathResolver;
    }
    @Override
    public void saveItem(UploadItemRequestDto item) throws BadRequestException {
        for (MultipartFile multipartFile: item.getMultipartFiles()) {

            if (multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().isBlank()) {
                throw new BadRequestException("Upload failed: file or folder name is empty");
            }

            try (InputStream itemStream = multipartFile.getInputStream()) {

                ObjectMetaDataDto objectMetaDataDto = ObjectMetaDataDto
                        .builder()
                        .contentType(multipartFile.getContentType())
                        .size(multipartFile.getResource().contentLength())
                        .build();

                StorageObjectWithMetaDataDto storageObjectWithMetaDataDto = StorageObjectWithMetaDataDto
                        .builder()
                        .absolutePath(item.getParentPath()+multipartFile.getOriginalFilename())
                        .inputStream(itemStream)
                        .objectMetaData(objectMetaDataDto)
                        .build();

                minioRepository.saveItem(storageObjectWithMetaDataDto);

            } catch (IOException e) {
                throw new BadRequestException("Upload failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void renameItem(String oldItemName, String newItemName)  {
        List<S3Path> oldItemsAbsolutePath = storagePathHelperUtil.getItemsAbsolutePath(oldItemName, true);
        for (S3Path oldItemAbsolutePath : oldItemsAbsolutePath) {
            ObjectMetaDataDto oldItemMetaData = minioRepository.getItemMetadata(oldItemAbsolutePath.getAbsolutePath());

            try (InputStream oldItemStream = minioRepository.downloadItem(oldItemAbsolutePath.getAbsolutePath())) {
                String newItemAbsolutePath = oldItemAbsolutePath.buildNewPath(oldItemName.endsWith("/"), newItemName);
                StorageObjectWithMetaDataDto storageObjectWithMetaDataDto = StorageObjectWithMetaDataDto
                        .builder()
                        .absolutePath(newItemAbsolutePath)
                        .inputStream(oldItemStream)
                        .objectMetaData(oldItemMetaData)
                        .build();

                minioRepository.saveItem(storageObjectWithMetaDataDto);

                removeItem(oldItemAbsolutePath.getAbsolutePath());
            } catch (IOException e) {
                throw new StorageException("Error while renaming items");
            }
        }
    }

    @Override
    public void removeItem(String itemName) {
        if (itemName.endsWith("/")) {
            List<S3Path> itemsAbsolutePath = storagePathHelperUtil.getItemsAbsolutePath(itemName, true);
            for (S3Path itemAbsolutePath : itemsAbsolutePath) {
                minioRepository.removeItem(itemAbsolutePath.getAbsolutePath());
            }
        } else {
            minioRepository.removeItem(itemName);
        }
    }

    @Override
    public List<SearchResultsDto> searchItem(String query, String userRootPath) {
        List<S3Path> allItems = storagePathHelperUtil.getItemsAbsolutePath(userRootPath, true);
        return filterMatchingItems(allItems, query);
    }

    private List<SearchResultsDto> filterMatchingItems(List<S3Path> allItems, String query) {
        String normalizedQuery = query.endsWith("/") ? query.substring(0, query.length() - 1) : query;
        Set<String> seenPaths = new HashSet<>();
        List<SearchResultsDto> results = new ArrayList<>();
        for (S3Path item : allItems) {
            String itemName = item.getItemName();
            String absolutePath = item.getAbsolutePath();
            String parentPath = item.getParentPath();
            boolean isDir = query.endsWith("/");
            boolean nameMatches = itemName.contains(normalizedQuery);
            boolean parentMatches = isDir && item.getLastFolderName().contains(normalizedQuery);

            if (nameMatches && seenPaths.add(absolutePath)) {
                results.add(buildResult(itemName, parentPath, isDir));
            }

            if (parentMatches && seenPaths.add(parentPath)) {
                String folderName = item.getLastFolderName();
                results.add(buildResult(folderName, parentPath, true));
            }
        }

        return results;
    }
    private SearchResultsDto buildResult(String name, String path, boolean isDirectory) {
        String userRootFolder = userPathResolver.getUserRootFolder();
        return SearchResultsDto.builder()
                .itemName(name)
                .displayPath(path.equals(userRootFolder) ? "/" : path)
                .isDirectory(isDirectory)
                .build();
    }

    @Override
    public List<S3ItemInfoDto> getAllItems(String S3UserItemsPath) {
        List<S3ItemInfoDto> items = new ArrayList<>();

        List<S3Path> itemsAbsolutePath = storagePathHelperUtil.getItemsAbsolutePath(S3UserItemsPath, false);

        for (S3Path itemAbsolutePath : itemsAbsolutePath) {
            String itemName = itemAbsolutePath.getItemName();
            String pathWithoutUserRootFolder = excludeRootUserFolderFromItemAbsolutePath(
                    itemAbsolutePath.getAbsolutePath()
            );
            S3ItemInfoDto itemInfo = S3ItemInfoDto.builder()
                    .itemName(itemName)
                    .absolutePath(pathWithoutUserRootFolder)
                    .isDirectory(itemAbsolutePath.isDirectory())
                    .build();
            items.add(itemInfo);
        }

        return items;
    }
    private String excludeRootUserFolderFromItemAbsolutePath(String itemAbsolutePath) {
        return itemAbsolutePath.substring(itemAbsolutePath.indexOf("/")+1);
    }
}
