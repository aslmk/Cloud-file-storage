package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.folder.DownloadFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;

import java.io.OutputStream;

public interface FolderService {
    void saveFolder(UploadFolderRequestDto request);
    void renameFolder(RenameFolderRequestDto request);
    void removeFolder(String folderFullPath);
    void downloadFolder(DownloadFolderRequestDto request, OutputStream outputStream);
    void createEmptyFolder(String currentDirectory, String folderName);
}
