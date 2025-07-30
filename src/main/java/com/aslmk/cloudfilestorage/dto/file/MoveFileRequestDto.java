package com.aslmk.cloudfilestorage.dto.file;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoveFileRequestDto {
    private String name;
    private String parentPath;
    private String targetPath;
}
