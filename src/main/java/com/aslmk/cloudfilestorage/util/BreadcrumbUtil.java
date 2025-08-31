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
        if (path == null || path.trim().replaceAll("/+", "").isEmpty()) {
            return Collections.emptyList();
        }

        path = path.replaceAll("\\+", "%2B");

        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        String normalizedPath = decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath;

        normalizedPath = normalizedPath.replaceAll("/+", "/");

        List<BreadcrumbDto> breadCrumbs = new ArrayList<>();

        String[] folders = normalizedPath.split("/");

        StringBuilder folderPath = new StringBuilder();

        for (String folder : folders) {
            folderPath.append(folder).append(URLEncoder.encode("/", StandardCharsets.UTF_8));
            BreadcrumbDto breadCrumb = BreadcrumbDto.builder()
                    .name(folder)
                    .fullPath(folderPath.toString())
                    .build();

            breadCrumbs.add(breadCrumb);
        }

        return breadCrumbs;
    }
}
