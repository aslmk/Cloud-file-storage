package com.aslmk.cloudfilestorage.s3;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinIoService {

    private final MinioClient minioClient;

    public MinIoService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void saveItem(String S3UserItemsPath, String itemName, InputStream itemStream) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket("user-files")
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
        }

        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket("user-files")
                .object(S3UserItemsPath + "/" + itemName)
                .stream(itemStream, itemStream.available(), -1)
                .build());
    }

    public List<S3ItemInfoDto> getAllItems(String S3UserItemsPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<S3ItemInfoDto> items = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket("user-files")
                .prefix(S3UserItemsPath)
                .build());

        for (Result<Item> result : results) {
            Item item = result.get();

            String itemName = getItemNameFromAbsolutePath(item.objectName());

            S3ItemInfoDto itemInfo = S3ItemInfoDto.builder()
                    .itemName(itemName)
                    .absolutePath(item.objectName())
                    .isDirectory(item.objectName().endsWith("/"))
                    .build();
            items.add(itemInfo);
        }

        return items;
    }

    public void removeItem(String itemName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (itemName.endsWith("/")) {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                    .builder()
                    .bucket("user-files")
                    .prefix(itemName)
                    .recursive(true)
                    .build());

            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(RemoveObjectArgs
                        .builder()
                        .bucket("user-files")
                        .object(item.objectName())
                        .build());
            }

        } else {
            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket("user-files")
                    .object(itemName)
                    .build());
        }

    }

    public void renameItem(String S3UserItemsPath, String oldItemName, String newItemName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket("user-files")
                .prefix(oldItemName)
                .recursive(true)
                .build());
        for (Result<Item> result : results) {
            Item item = result.get();
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket("user-files")
                    .object(item.objectName())
                    .build());

            try (InputStream oldFileStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("user-files")
                            .object(item.objectName())
                            .build()
            )){
                String newItemPath = S3UserItemsPath + newItemName;
                String currentFileName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);

                String res = newItemPath.endsWith("/") ? (newItemPath+"/"+currentFileName) : newItemPath;

                minioClient.putObject(PutObjectArgs.builder()
                        .bucket("user-files")
                        .object(res)
                        .stream(oldFileStream, stat.size(), -1)
                        .contentType(stat.contentType())
                        .build());
            }
            removeItem(item.objectName());
        }
    }

    public List<SearchResultsDto> searchItems(String query, String S3UserItemsPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<SearchResultsDto> searchResults = new ArrayList<>();

        Iterable<Result<Item>> rootFolder = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket("user-files")
                        .prefix(S3UserItemsPath)
                        .build()
        );

        traverseAndSearchBySuffix(rootFolder, searchResults, query);

        return searchResults;
    }

    private void traverseAndSearchBySuffix(Iterable<Result<Item>> itemResults, List<SearchResultsDto> searchResults, String nameSuffix) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (Result<Item> itemResult : itemResults) {
            Item item = itemResult.get();

            if(item.objectName().endsWith(nameSuffix)) {
                SearchResultsDto searchResult = SearchResultsDto.builder()
                        .itemName(nameSuffix)
                        .displayPath(item.objectName().substring(0, item.objectName().lastIndexOf(nameSuffix)))
                        .absolutePath(item.objectName())
                        .isDirectory(item.objectName().endsWith("/"))
                        .build();
                searchResults.add(searchResult);
            }

            if (item.objectName().endsWith("/")) {
                Iterable<Result<Item>> nestedItems = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket("user-files")
                                .prefix(item.objectName())
                                .build());
                traverseAndSearchBySuffix(nestedItems, searchResults, nameSuffix);
            }
        }
    }

    private String getItemNameFromAbsolutePath(String item) {
        String itemName;
        if (item.endsWith("/")) {
            itemName = item.substring(item.lastIndexOf('/', item.lastIndexOf('/') - 1) + 1, item.lastIndexOf('/'));
        } else {
            itemName = item.substring(item.lastIndexOf('/') + 1);
        }
        return itemName;
    }
}
