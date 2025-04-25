package com.aslmk.cloudfilestorage.dto;

import lombok.*;

import java.io.InputStream;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadItemRequestDto {
    private String itemName;
    private InputStream itemInputStream;
    private String absolutePath;
}
