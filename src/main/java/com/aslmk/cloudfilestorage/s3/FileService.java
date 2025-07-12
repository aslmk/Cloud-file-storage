package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.file.RenameFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;

public interface FileService {
    void saveFile(UploadFileRequestDto request);
    void renameFile(RenameFileRequestDto request);
    void removeFile(String fileFullPath);
}
