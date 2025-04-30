package com.aslmk.cloudfilestorage.util;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class UserPathResolver {

    public String resolveUserS3Path(String path, long userId) {
        String S3UserItemsPath = String.format("user-%s-files/", userId);
        if (path != null && !path.isEmpty()) {
            S3UserItemsPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        }
        return S3UserItemsPath;
    }
}
