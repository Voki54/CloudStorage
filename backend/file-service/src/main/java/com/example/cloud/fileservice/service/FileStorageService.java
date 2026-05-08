package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.config.S3Config;
import com.example.cloud.fileservice.exception.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

import static com.example.cloud.fileservice.util.S3KeyGenerator.generateKey;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;
    private final S3Config config;

    @PostConstruct
    public void init() {
        String bucket = config.getBucket();
        try {
            if (bucketExists(bucket)) {
                log.debug("Bucket '{}' already exists", bucket);
                return;
            }
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucket)
                    .build());
            log.debug("Bucket '{}' created successfully", bucket);
        } catch (S3Exception e) {
            log.error("S3 error for bucket '{}': {} - {}",
                    bucket,
                    e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().errorMessage(),
                    e);
        } catch (Exception e) {
            log.error("Unexpected error during bucket initialization for '{}'", bucket, e);
            throw new StorageException("Unexpected error during bucket initialization", e);
        }
        log.debug("S3FileStorageService initialization completed");
    }

    public String uploadFile(MultipartFile file, String originalFilename, String contentType, long fileSize,
                             String ownerId) {

        String key = generateKey(ownerId, originalFilename);

        log.debug("Starting file upload: key='{}', size={}, ownerId={}", key, fileSize, ownerId);

        try (InputStream input = file.getInputStream()) {
            long startTime = System.currentTimeMillis();

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(config.getBucket())
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(input, fileSize));

            log.debug("File uploaded successfully: key='{}', duration={}ms",
                    key, System.currentTimeMillis() - startTime);
            return key;
        } catch (Exception e) {
            log.error("Error uploading file: name='{}', key='{}'",
                    originalFilename, key, e);
            throw new FileUploadException(key, ownerId, e);
        }
    }

    public InputStream getFileStream(String key) {
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(key)
                    .build());
        } catch (NoSuchKeyException e) {
            log.warn("File not found: key='{}'", key);
            throw new NotFoundException(key, e);
        } catch (Exception e) {
            log.error("Error downloading file: key='{}'", key, e);
            throw new FileDownloadException(key, e);
        }
    }

    public void deleteFile(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("S3 key must not be null or empty");
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(key)
                    .build());
            log.debug("File '{}' deleted successfully from S3", key);
        } catch (S3Exception e) {
            log.error("Failed to delete file from S3. key='{}'", key, e);
            throw new FileDeleteException(key, e);
        }
    }

    private boolean bucketExists(String bucket) {
        try {
            s3Client.headBucket(b -> b.bucket(bucket));
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }
}