package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.*;
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

            try {
                StorableFileDto storableFile = StorableFileDto.builder()
                        .absolutePath(item.getParentPath()+multipartFile.getOriginalFilename())
                        .inputStream(multipartFile.getInputStream())
                        .contentType(multipartFile.getContentType())
                        .size(multipartFile.getResource().contentLength())
                        .build();
                minioRepository.saveItem(storableFile);
            } catch (IOException e) {
                throw new BadRequestException("Upload failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void renameItem(String oldItemFullPath, String newItemName)  {
        List<S3Path> oldItemsAbsolutePath = storagePathHelperUtil.getItemsAbsolutePath(oldItemFullPath, true);

        for (S3Path oldItemAbsolutePath : oldItemsAbsolutePath) {

            try (InputStream oldItemStream = minioRepository.downloadItem(oldItemAbsolutePath.absolutePath())) {
                String oldItemName = new S3Path(oldItemFullPath).getItemName();
                String newItemAbsolutePath = oldItemAbsolutePath.buildNewPath(newItemName, oldItemName);

                FileMetadata metadata = minioRepository.getFileMetadata(oldItemAbsolutePath.absolutePath());

                StorableFileDto storableFile = StorableFileDto.builder()
                        .absolutePath(newItemAbsolutePath)
                        .inputStream(oldItemStream)
                        .contentType(metadata.contentType())
                        .size(metadata.size())
                        .build();

                minioRepository.saveItem(storableFile);

                removeItem(oldItemAbsolutePath.absolutePath());
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
                minioRepository.removeItem(itemAbsolutePath.absolutePath());
            }
        } else {
            minioRepository.removeItem(itemName);
        }
    }
}
