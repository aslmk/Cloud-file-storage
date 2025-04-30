package com.aslmk.cloudfilestorage.dto;

import java.io.InputStream;

public interface StorageObject {
    String getAbsolutePath();
    InputStream getInputStream();
}
