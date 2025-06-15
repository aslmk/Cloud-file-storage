package com.aslmk.cloudfilestorage.repository;

import com.aslmk.cloudfilestorage.dto.ObjectMetaDataDto;
import com.aslmk.cloudfilestorage.dto.StorageObjectWithMetaDataDto;
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
    public void createBucketIfNotExists() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket(bucketName)
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

    }

    public void saveItem(StorageObjectWithMetaDataDto item) {
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(item.getAbsolutePath())
                    .stream(item.getInputStream(), item.getObjectMetaData().getSize(), -1)
                    .contentType(item.getObjectMetaData().getContentType())
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
    public Iterable<Result<Item>> listItems(String folder, boolean recursively) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(bucketName)
                .prefix(folder)
                .recursive(recursively)
                .build());
    }
    private StatObjectResponse getStatObject(String itemAbsolutePath) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(itemAbsolutePath)
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException("Unable to get object metadata");
        }
    }
    public ObjectMetaDataDto getItemMetadata(String itemAbsolutePath) {
        StatObjectResponse statObject = getStatObject(itemAbsolutePath);
        return ObjectMetaDataDto
                .builder()
                .size(statObject.size())
                .contentType(statObject.contentType())
                .build();
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
