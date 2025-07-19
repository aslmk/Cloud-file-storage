package com.aslmk.cloudfilestorage.util;

import com.aslmk.cloudfilestorage.dto.BreadcrumbDto;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class BreadcrumbUtil {

    public List<BreadcrumbDto> getBreadcrumb(String path) {

        if (path == null || path.isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;

        String decodedPath = URLDecoder.decode(normalizedPath, StandardCharsets.UTF_8);

        List<BreadcrumbDto> breadCrumbs = new ArrayList<>();

        String[] folders = decodedPath.split("/");

        StringBuilder folderPath = new StringBuilder();

        for (String folder : folders) {
            folderPath.append(folder).append("/");
            String encodedFolderPath = URLEncoder.encode(folderPath.toString(), StandardCharsets.UTF_8);
            BreadcrumbDto breadCrumb = BreadcrumbDto.builder()
                    .name(folder)
                    .fullPath(encodedFolderPath)
                    .build();

            breadCrumbs.add(breadCrumb);
        }

        return breadCrumbs;
    }
}
