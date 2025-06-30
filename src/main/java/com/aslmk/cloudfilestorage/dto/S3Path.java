package com.aslmk.cloudfilestorage.dto;

public record S3Path(String absolutePath) {

    public boolean isDirectory() {
        return absolutePath.endsWith("/");
    }

    public String getParentPath() {
        String normalizedPath = normalizePath(absolutePath);
        int lastSlashIndex = normalizedPath.lastIndexOf("/");
        return lastSlashIndex >= 0 ? normalizedPath.substring(0, lastSlashIndex + 1) : "";
    }

    public String getItemName() {
        String normalizedPath = normalizePath(absolutePath);
        return extractName(normalizedPath);
    }

    private String normalizePath(String path) {
        return path.endsWith("/")
                ? path.substring(0, path.length() - 1)
                : path;
    }

    private String extractName(String normalizedPath) {
        int lastSeparatorIndex = normalizedPath.lastIndexOf("/");
        return lastSeparatorIndex >= 0
                ? normalizedPath.substring(lastSeparatorIndex + 1)
                : normalizedPath;
    }

    public String getLastFolderName() {
        String parentPath = getParentPath();
        return extractName(normalizePath(parentPath));
    }

    public String buildNewPath(String newItemName, String oldItemName) {
        return absolutePath.replaceFirst(oldItemName, newItemName);
    }
}
