package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.StorableFileDto;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.DownloadFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;
import com.aslmk.cloudfilestorage.exception.BadRequestException;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FolderServiceImpl implements FolderService {

    @Value("${empty-folder-marker}")
    private String EMPTY_FOLDER;

    private final MinioRepository minioRepository;
    private final DirectoryListingService directoryListingService;
    private final FileService fileService;
    private final UserPathResolver userPathResolver;

    public FolderServiceImpl(MinioRepository minioRepository,
                             DirectoryListingService directoryListingService,
                             FileService fileService, UserPathResolver userPathResolver) {
        this.minioRepository = minioRepository;
        this.directoryListingService = directoryListingService;
        this.fileService = fileService;
        this.userPathResolver = userPathResolver;
    }

    @Override
    public void saveFolder(UploadFolderRequestDto request) {
        for (MultipartFile multipartFile: request.getMultipartFiles()) {

            UploadFileRequestDto uploadFile = UploadFileRequestDto
                    .builder()
                    .multipartFile(multipartFile)
                    .parentPath(request.getParentPath())
                    .build();

            fileService.saveFile(uploadFile);
        }
    }

    @Override
    public void renameFolder(RenameFolderRequestDto request) {
        String folderFullPath = request.getParentPath()+request.getOldFolderName();

        List<S3Path> listFiles = directoryListingService.listS3Paths(folderFullPath, true);

        for (S3Path file : listFiles) {
            String tmp = file.absolutePath().replace(request.getParentPath(), "");

            String newFolderPath = new S3Path(tmp)
                    .buildNewPath(request.getNewFolderName(), request.getOldFolderName());

            String newFullPath = request.getParentPath()+newFolderPath;

            minioRepository.copyItem(newFullPath, file.absolutePath());
        }

        removeFolder(folderFullPath);
    }

    @Override
    public void removeFolder(String folderFullPath) {
        if (!folderFullPath.endsWith("/")) {
            throw new RuntimeException("Folder path is not valid");
        }

        directoryListingService
                .listS3Paths(folderFullPath, true)
                .forEach(item ->
                        minioRepository.removeItem(item.absolutePath())
                );
    }

    @Override
    public void downloadFolder(DownloadFolderRequestDto request, OutputStream outputStream) {
        String folder = request.getParentPath() + request.getFolderName();
        List<S3Path> files = directoryListingService.listS3Paths(folder, true);

        try (ZipOutputStream zout = new ZipOutputStream(outputStream)) {

            for (S3Path file : files) {
                String normalizedFilePath = file.absolutePath()
                        .replace(userPathResolver.getUserRootFolder(), "");

                if (normalizedFilePath.endsWith(EMPTY_FOLDER)) {
                    normalizedFilePath = normalizedFilePath.replace(EMPTY_FOLDER, "/");
                }

                ZipEntry zipEntry = new ZipEntry(normalizedFilePath);

                zout.putNextEntry(zipEntry);

                try (InputStream inputStream = minioRepository.downloadItem(file.absolutePath())) {
                    zout.write(StreamUtils.copyToByteArray(inputStream));
                } catch (IOException e) {
                    throw new BadRequestException(
                            String.format("Failed to download file '%s'. Please try again", file.getItemName())
                    );
                }

                zout.closeEntry();
            }
        } catch (IOException e) {
            throw new BadRequestException(
                    String.format("Failed to download folder '%s'. Please try again", request.getFolderName())
            );
        }
    }

    @Override
    public void createEmptyFolder(String currentDirectory, String folderName) {

        if (folderName == null || folderName.isBlank()) {
            throw new BadRequestException("Folder name cannot be empty");
        }

        String normalizedFolderName = folderName.replaceAll("/+$", "");

        if (normalizedFolderName.trim().isEmpty()) {
            throw new BadRequestException("Folder name is invalid");
        }

        StorableFileDto storableFile = StorableFileDto.builder()
                .absolutePath(currentDirectory + normalizedFolderName + EMPTY_FOLDER)
                .inputStream(InputStream.nullInputStream())
                .size(0)
                .contentType("text/plain")
                .build();

        minioRepository.saveItem(storableFile);
    }
}
