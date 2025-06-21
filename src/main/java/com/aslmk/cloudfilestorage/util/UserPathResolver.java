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
        if (path != null && !path.isEmpty()) {
            if (path.equals("/")) {
                return S3UserItemsPath;
            }
            String normalizedPath = path.replaceFirst("/", "");
            String decodedPath = URLDecoder.decode(normalizedPath, StandardCharsets.UTF_8);
            S3userPath.append(decodedPath);
        }
        return S3userPath.toString();
    }

    public String encodeUserS3Path(String path) {
        String userRootFolder = getUserRootFolder();
        if (path != null && !path.isEmpty()) {
            String res = path.replace(userRootFolder, "");
            return URLEncoder.encode(res, StandardCharsets.UTF_8);
        } else {
            return "";
        }
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
