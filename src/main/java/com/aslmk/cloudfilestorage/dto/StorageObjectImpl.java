package com.aslmk.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.InputStream;

@AllArgsConstructor
@Builder
public class StorageObjectImpl implements StorageObject {
    private String absolutePath;
    private  InputStream inputStream;


    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }
}
