package com.aslmk.cloudfilestorage.dto;

public class S3PathHelper {

    private final String absolutePath;

    public S3PathHelper(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public boolean isDirectory() {
        return absolutePath.endsWith("/");
    }

    public String getParentPath() {
        int lastSlashIndex = absolutePath.lastIndexOf('/', absolutePath.endsWith("/") ? absolutePath.length() - 2 : absolutePath.length() - 1);
        return lastSlashIndex != -1 ? absolutePath.substring(0, lastSlashIndex + 1) : "";
    }

    public String getItemName() {
        String itemName;
        if (absolutePath.endsWith("/")) {
            itemName = absolutePath.substring(absolutePath.lastIndexOf('/', absolutePath.lastIndexOf('/') - 1) + 1, absolutePath.lastIndexOf('/'));
        } else {
            itemName = absolutePath.substring(absolutePath.lastIndexOf('/') + 1);
        }
        return itemName;
    }

    public String getLastFolderName() {
        String parentPath = getParentPath();
        if (parentPath.isEmpty()) {
            return "";
        }
        int lastSlashIndex = parentPath.lastIndexOf("/", parentPath.length() - 2);
        if (lastSlashIndex == -1) {
            return parentPath;
        }
        return parentPath.substring(lastSlashIndex + 1);
    }

    public String buildNewPath(boolean isFolderRename , String newItemName) {
        if (isFolderRename) {
            String parentPath = getParentPath();
            String oldFolderName = parentPath.substring(parentPath.lastIndexOf('/', parentPath.length() - 2) + 1, parentPath.length() - 1);
            String grandParentPath = parentPath.substring(0, parentPath.lastIndexOf(oldFolderName + '/'));
            String fileName = absolutePath.substring(parentPath.length());
            return grandParentPath + newItemName + fileName;
        } else {
            return getParentPath() + newItemName;
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
