package com.aslmk.cloudfilestorage.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class S3ItemInfoDto {
    private String itemName;
    private String absolutePath;
    private boolean isDirectory;
}

