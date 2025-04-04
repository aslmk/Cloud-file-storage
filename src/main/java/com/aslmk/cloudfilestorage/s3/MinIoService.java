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

    public void saveFile(String S3filePath, String fileName, InputStream fileStream) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

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
                .object(S3filePath + "/" + fileName)
                .stream(fileStream, fileStream.available(), -1)
                .build());
    }

    public List<String> getAllFiles(String S3filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<String> objects = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket("user-files")
                .prefix(S3filePath + "/")
                .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            objects.add(item.objectName());
        }

        return objects;
    }

    public void removeFile(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket("user-files")
                .object(fileName)
                .build());
    }

    public void renameFile(String S3FilePath, String oldFileName, String newFileName) throws  ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException{
        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                .bucket("user-files")
                .object(S3FilePath + "/" + oldFileName)
                .build());

        try (InputStream oldFileStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("user-files")
                        .object(S3FilePath + "/" + oldFileName)
                        .build()
        )){
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("user-files")
                    .object(S3FilePath + "/" + newFileName)
                    .stream(oldFileStream, stat.size(), -1)
                    .contentType(stat.contentType())
                    .build());
            removeFile(oldFileName);
        }
    }
}
