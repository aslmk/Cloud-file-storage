package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.StorableFileDto;
import com.aslmk.cloudfilestorage.dto.file.DownloadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.RenameFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;
import com.aslmk.cloudfilestorage.exception.BadRequestException;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService {

    private final MinioRepository minioRepository;

    public FileServiceImpl(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    @Override
    public void saveFile(UploadFileRequestDto request) {

        MultipartFile multipartFile = request.getMultipartFile();

        if (multipartFile == null) {
            throw new BadRequestException("File is empty");
        }

        if (multipartFile.getOriginalFilename() == null ||
                multipartFile.getOriginalFilename().isBlank()) {
            throw new BadRequestException("Upload failed: file or folder name is empty");
        }

        try {
            StorableFileDto storableFile = StorableFileDto.builder()
                    .absolutePath(request.getParentPath()+multipartFile.getOriginalFilename())
                    .inputStream(multipartFile.getInputStream())
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getResource().contentLength())
                    .build();
            minioRepository.saveItem(storableFile);
        } catch (IOException e) {
            throw new BadRequestException("Upload failed: " + e.getMessage());
        }
    }

    @Override
    public void renameFile(RenameFileRequestDto request)  {
        String oldFullPath = request.getParentPath()+request.getOldFileName();

        String newFullPath = new S3Path(oldFullPath)
                .buildNewPath(request.getNewFileName(), request.getOldFileName());

        minioRepository.copyItem(newFullPath, oldFullPath);
        removeFile(oldFullPath);
    }

    @Override
    public void removeFile(String fileFullPath)  {
        minioRepository.removeItem(fileFullPath);
    }

    @Override
    public Resource downloadFile(DownloadFileRequestDto request) {
        String fullPath = request.getParentPath()+request.getFileName();
        byte[] bytes;
        try (InputStream inputStream = minioRepository.downloadItem(fullPath)) {
            bytes = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new BadRequestException(
                    String.format("Failed to download file '%s'. Please try again", request.getFileName())
            );
        }
        return new ByteArrayResource(bytes);
    }
}
