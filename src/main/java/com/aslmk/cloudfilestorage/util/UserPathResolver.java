package com.aslmk.cloudfilestorage.util;

import com.aslmk.cloudfilestorage.exception.UnauthorizedAccessException;
import com.aslmk.cloudfilestorage.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UserPathResolver {

    private static final String USER_ROOT_FOLDER = "user-%d-files/";

    public String resolveUserS3Path(String path) {
        String S3UserItemsPath = getUserRootFolder();
        StringBuilder S3userPath = new StringBuilder(S3UserItemsPath);

        if (path == null) {
            return S3userPath.toString();
        }

        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        if (decodedPath.replaceAll("/+", "").trim().isEmpty()) {
            return S3UserItemsPath;
        }

        String normalizedPath = decodedPath.replaceAll("/+", "/");

        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        S3userPath.append(normalizedPath);

        return URLDecoder.decode(S3userPath.toString(), StandardCharsets.UTF_8);
    }

    public String encodeUserS3Path(String path) {
        String userRootFolder = getUserRootFolder();

        if (path == null) {
            return "";
        }

        String excludeRootFolderFromPath = path.replace(userRootFolder, "");

        String normalizedPath = excludeRootFolderFromPath.replaceAll("/+", "/");

        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        if (normalizedPath.replaceAll("/+", "").trim().isEmpty()) {
            return "";
        }

        return URLEncoder.encode(normalizedPath, StandardCharsets.UTF_8);
    }

    public String getUserRootFolder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Unauthorized access");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            throw new IllegalStateException("Unexpected principal type: " + principal);
        }

        return String.format(USER_ROOT_FOLDER, customUserDetails.getUserEntity().getId());
    }
}
