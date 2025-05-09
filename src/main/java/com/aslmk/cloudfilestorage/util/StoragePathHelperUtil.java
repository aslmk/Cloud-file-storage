package com.aslmk.cloudfilestorage.util;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.exception.StorageException;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StoragePathHelperUtil {

    private final MinioRepository minioRepository;

    public StoragePathHelperUtil(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    public List<S3Path> getItemsAbsolutePath(String folder, boolean recursively) {
        try {
            List<S3Path> items = new ArrayList<>();

            Iterable<Result<Item>> results = minioRepository.getStorageObjectsList(folder, recursively);

            for (Result<Item> result : results) {
                Item item = result.get();
                String absolutePath = item.objectName();
                items.add(new S3Path(absolutePath));

            }

            return items;
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException("Error while fetching data from storage");
        }
    }

    public String normalizeS3ObjectName(String oldItemName, String newItemName) {
        if (oldItemName.endsWith("/")) {
            newItemName = newItemName.replaceAll("/+$","");
            newItemName += "/";
        }
        return newItemName;
    }

    public String extractFolderName(String folderPath) {
        String trimmed = folderPath.endsWith("/") ? folderPath.substring(0, folderPath.length() - 1) : folderPath;
        int lastSlash = trimmed.lastIndexOf('/');
        return lastSlash >= 0 ? trimmed.substring(lastSlash + 1) : trimmed;
    }

}
