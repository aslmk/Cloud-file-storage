package com.aslmk.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ObjectMetaDataDto {
    private String contentType;
    private long size;
}
