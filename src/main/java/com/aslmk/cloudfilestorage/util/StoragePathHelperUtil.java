package com.aslmk.cloudfilestorage.util;

import com.aslmk.cloudfilestorage.dto.S3PathHelper;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class StoragePathHelperUtil {

    private final MinioRepository minioRepository;

    public StoragePathHelperUtil(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    public List<S3PathHelper> getItemsAbsolutePath(String folder, boolean recursively) {
        try {
            List<S3PathHelper> items = new ArrayList<>();
            Set<String> folders = new HashSet<>();

            Iterable<Result<Item>> results = minioRepository.getStorageObjectsList(folder, recursively);

            for (Result<Item> result : results) {
                Item item = result.get();
                String absolutePath = item.objectName();
                items.add(new S3PathHelper(absolutePath));

                String[] parts = absolutePath.split("/");
                StringBuilder currentPath = new StringBuilder();
                for (int i = 0; i < parts.length - 1; i++) {
                    currentPath.append(parts[i]).append("/");
                    folders.add(currentPath.toString());
                }
            }

            for (String folderPath : folders) {
                items.add(new S3PathHelper(folderPath));
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

}
