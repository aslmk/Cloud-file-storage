package com.aslmk.cloudfilestorage.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetFolderDto {
    private String name;
    private String path;
}
