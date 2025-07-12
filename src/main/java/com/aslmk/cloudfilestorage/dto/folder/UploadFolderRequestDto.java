package com.aslmk.cloudfilestorage.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UploadFolderRequestDto {
    private MultipartFile[] multipartFiles;
    private String parentPath;
}
