package com.aslmk.cloudfilestorage.service.Impl;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.exception.StorageException;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryListingServiceImpl implements DirectoryListingService {

    @Value("${empty-folder-marker}")
    private String EMPTY_FOLDER;

    private final MinioRepository minioRepository;

    public DirectoryListingServiceImpl(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    @Override
    public List<S3ItemInfoDto> listItems(String path) {
        List<S3Path> userItems = listS3Paths(path, false);
        return userItems.stream()
                .map(this::toDto)
                .filter(item -> !item.getAbsolutePath().endsWith(EMPTY_FOLDER))
                .collect(Collectors.toList());
    }

    @Override
    public List<S3Path> listS3Paths(String folder, boolean recursively) {
        try {
            List<S3Path> items = new ArrayList<>();

            Iterable<Result<Item>> results = minioRepository.listItems(folder, recursively);

            for (Result<Item> result : results) {
                Item item = result.get();
                String absolutePath = item.objectName();
                items.add(new S3Path(absolutePath));
            }

            return items;
        } catch (InsufficientDataException | ErrorResponseException | IOException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidResponseException | XmlParserException | InternalException |
                 ServerException e) {
            throw new StorageException("Error while fetching data from storage");
        }
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
