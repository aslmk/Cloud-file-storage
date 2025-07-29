package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.file.DownloadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.RenameFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;
import org.springframework.core.io.Resource;

public interface FileService {
    void saveFile(UploadFileRequestDto request);
    void renameFile(RenameFileRequestDto request);
    void removeFile(String fileFullPath);
    Resource downloadFile(DownloadFileRequestDto request);
    void moveFile(String currentFilePath, String newFilePath);
}
