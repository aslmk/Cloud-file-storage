package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;

public interface FolderService {
    void saveFolder(UploadFolderRequestDto request);
    void renameFolder(RenameFolderRequestDto request);
    void removeFolder(String folderFullPath);
}
