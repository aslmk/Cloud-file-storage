package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.ObjectMetaDataDto;
import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.StorageObjectWithMetaDataDto;
import com.aslmk.cloudfilestorage.dto.UploadItemRequestDto;
import com.aslmk.cloudfilestorage.exception.StorageException;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import com.aslmk.cloudfilestorage.util.StoragePathHelperUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class MinioServiceImpl implements StorageService {

    private final StoragePathHelperUtil storagePathHelperUtil;
    private final MinioRepository minioRepository;

    public MinioServiceImpl(StoragePathHelperUtil storagePathHelperUtil, MinioRepository minioRepository) {
        this.storagePathHelperUtil = storagePathHelperUtil;
        this.minioRepository = minioRepository;
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
}
