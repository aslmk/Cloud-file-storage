package com.aslmk.cloudfilestorage.dto.folder;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RenameFolderRequestDto {
    private String parentPath;
    private String oldFolderName;
    private String newFolderName;
}