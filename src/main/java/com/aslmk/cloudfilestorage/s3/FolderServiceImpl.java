package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import com.aslmk.cloudfilestorage.util.StoragePathHelperUtil;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    private final MinioRepository minioRepository;
    private final StoragePathHelperUtil storagePathHelperUtil;
    private final FileService fileService;

    public FolderServiceImpl(MinioRepository minioRepository,
                             StoragePathHelperUtil storagePathHelperUtil,
                             FileService fileService) {
        this.minioRepository = minioRepository;
        this.storagePathHelperUtil = storagePathHelperUtil;
        this.fileService = fileService;
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

        List<S3Path> listFiles = storagePathHelperUtil.getItemsAbsolutePath(folderFullPath, true);

        for (S3Path file : listFiles) {
            String tmp = file.absolutePath().replace(request.getParentPath(), "");

            String newFolderPath = new S3Path(tmp)
                    .buildNewPath(request.getNewFolderName(), request.getOldFolderName());

            String newFullPath = request.getParentPath()+newFolderPath;

            minioRepository.renameItem(newFullPath, file.absolutePath());
        }

        removeFolder(folderFullPath);
    }

    @Override
    public void removeFolder(String folderFullPath) {
        if (!folderFullPath.endsWith("/")) {
            throw new RuntimeException("Folder path is not valid");
        }

        storagePathHelperUtil
                .getItemsAbsolutePath(folderFullPath, true)
                .forEach(item ->
                        minioRepository.removeItem(item.absolutePath())
                );
    }
}
