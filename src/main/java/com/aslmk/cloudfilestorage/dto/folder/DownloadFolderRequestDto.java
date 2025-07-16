package com.aslmk.cloudfilestorage.dto.folder;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DownloadFolderRequestDto {
    private String folderName;
    private String parentPath;
}

