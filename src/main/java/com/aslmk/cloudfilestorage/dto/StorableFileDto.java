package com.aslmk.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class StorableFileDto {
    private String absolutePath;
    private InputStream inputStream;
    private String contentType;
    private long size;
}