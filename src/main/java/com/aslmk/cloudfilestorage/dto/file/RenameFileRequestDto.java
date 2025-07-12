package com.aslmk.cloudfilestorage.dto.file;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RenameFileRequestDto {
    private String parentPath;
    private String oldFileName;
    private String newFileName;
}
