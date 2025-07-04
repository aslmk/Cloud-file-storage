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
public class StorableFileDto implements StorageObject {

    private String absolutePath;
    private InputStream inputStream;
    private String contentType;
    private long size;

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }
}