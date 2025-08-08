package com.aslmk.cloudfilestorage.repository;

import com.aslmk.cloudfilestorage.dto.StorableFileDto;
import com.aslmk.cloudfilestorage.exception.StorageException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Repository
public class MinioRepository {

    @Value("${minio.bucketName}")
    private String bucketName;
    private final MinioClient minioClient;

    public MinioRepository(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void createBucketIfNotExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs
                    .builder()
                    .bucket(bucketName)
                    .build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException("Bucket initialization failed");
        }
    }

    public void saveItem(StorableFileDto item) {
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(item.getAbsolutePath())
                    .stream(item.getInputStream(), item.getSize(), -1)
                    .contentType(item.getContentType())
                    .build());
        }  catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                  NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                  InternalException e) {
            throw new StorageException("Error while saving data to storage");
        }
    }

    public void removeItem(String itemName) {
        try {
            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(itemName)
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                    NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                    InternalException e) {
            throw new StorageException("Error while removing data from storage");
        }
    }

    public void copyItem(String newFullPath, String oldFullPath) {
        try {
            minioClient.copyObject(CopyObjectArgs
                    .builder()
                            .bucket(bucketName)
                            .object(newFullPath)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(oldFullPath)
                                    .build())
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException("Error while renaming item");
        }
    }

    public Iterable<Result<Item>> listItems(String folder, boolean recursively) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(bucketName)
                .prefix(folder)
                .recursive(recursively)
                .build());
    }

    public InputStream downloadItem(String itemAbsolutePath) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(itemAbsolutePath)
                            .build());
        }  catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                  NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                  InternalException e) {
            throw new StorageException("Unable to download object from storage");
        }
    }

}
