package com.aslmk.cloudfilestorage.dto;

public class S3Path {

    private final String absolutePath;

    public S3Path(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public boolean isDirectory() {
        return absolutePath.endsWith("/");
    }
    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getParentPath() {
        String normalizedPath = normalizePath(absolutePath);
        int lastSlashIndex = normalizedPath.lastIndexOf("/");
        return lastSlashIndex >= 0 ? normalizedPath.substring(0, lastSlashIndex+1) : "";
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

    public String buildNewPath(boolean isFolderRename , String newItemName) {
        return isFolderRename ? buildFolderPath(newItemName) : buildFilePath(newItemName);
    }
    private String buildFolderPath(String newFolderName) {
        String parentPath = getParentPath();
        String currentFolderName = getLastFolderName();
        String grandParentPath = extractGrandParentPath(parentPath, currentFolderName);
        String fileName = extractFileName(parentPath);
        return grandParentPath + newFolderName + fileName;
    }
    private String buildFilePath(String newItemName) {
        return getParentPath() + newItemName;
    }
    private String extractGrandParentPath(String parentPath, String folderName) {
        String folderNameWithSeparator = folderName + "/";
        int folderStartIndex = parentPath.lastIndexOf(folderNameWithSeparator);
        return folderStartIndex >= 0
                ? parentPath.substring(0, folderStartIndex)
                : parentPath;
    }
    private String extractFileName(String parentPath) {
        return absolutePath.startsWith(parentPath)
                ? absolutePath.substring(parentPath.length())
                : "";
    }
}
