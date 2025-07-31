package com.aslmk.cloudfilestorage.dto.folder;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoveFolderRequestDto {
    private String name;
    private String parentPath;
    private String targetPath;
}
