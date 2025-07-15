package com.aslmk.cloudfilestorage.dto.file;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DownloadFileRequestDto {
    private String fileName;
    private String parentPath;
}
