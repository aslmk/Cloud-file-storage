package com.aslmk.cloudfilestorage.s3;

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

    public void saveItem(String S3ItemPath, String itemName, InputStream itemStream) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

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
                .object(S3ItemPath + "/" + itemName)
                .stream(itemStream, itemStream.available(), -1)
                .build());
    }

    public List<String> getAllItems(String S3ItemPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<String> objects = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket("user-files")
                .prefix(S3ItemPath + "/")
                .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            objects.add(item.objectName().substring(S3ItemPath.length()+1));
        }

        return objects;
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

    public void renameItem(String S3ItemPath, String oldItemName, String newItemName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket("user-files")
                .prefix(S3ItemPath + "/" + oldItemName)
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
                String newItemPath = item.objectName().replace(oldItemName, newItemName);
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket("user-files")
                        .object(newItemPath)
                        .stream(oldFileStream, stat.size(), -1)
                        .contentType(stat.contentType())
                        .build());
            }
            removeItem(item.objectName());
        }
    }
}
