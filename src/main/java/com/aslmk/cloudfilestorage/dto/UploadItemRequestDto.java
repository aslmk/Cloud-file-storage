package com.aslmk.cloudfilestorage.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadItemRequestDto {
    private MultipartFile[] multipartFiles;
    private String parentPath;
}
