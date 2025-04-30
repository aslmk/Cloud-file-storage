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

    public String buildNewPath(String newItemName) {
        String oldItemNameFromAbsolutePath = getItemName();
        return absolutePath.replace(oldItemNameFromAbsolutePath, newItemName);
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
