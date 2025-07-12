package com.aslmk.cloudfilestorage.dto.file;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UploadFileRequestDto {
    private MultipartFile multipartFile;
    private String parentPath;
}
