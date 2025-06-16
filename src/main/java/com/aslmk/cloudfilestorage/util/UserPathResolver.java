package com.aslmk.cloudfilestorage.util;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UserPathResolver {

    private static final String USER_ROOT_FOLDER = "user-%d-files/";

    public String resolveUserS3Path(String path, long userId) {
        String S3UserItemsPath = getUserRootFolder(userId);
        StringBuilder S3userPath = new StringBuilder(S3UserItemsPath);
        if (path != null && !path.isEmpty()) {
            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
            S3userPath.append(decodedPath);
        }
        return S3userPath.toString();
    }

    public String encodeUserS3Path(String path, Long userId) {
        String userRootFolder = getUserRootFolder(userId);
        if (path != null && !path.isEmpty()) {
            String res = path.replace(userRootFolder, "");
            return URLEncoder.encode(res, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    public String getUserRootFolder(Long userId) {
        return String.format(USER_ROOT_FOLDER, userId);
    }
}
