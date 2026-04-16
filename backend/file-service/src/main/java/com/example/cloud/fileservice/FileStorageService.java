package com.example.cloud.fileservice;

import com.example.cloud.fileservice.config.S3Config;
import com.example.cloud.fileservice.exception.FileUploadException;
import com.example.cloud.fileservice.exception.StorageException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Base64;

import static com.example.cloud.fileservice.util.S3KeyGenerator.generateKey;

//import static com.example.fileservice.util.S3KeyGenerator.generateKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;
    private final S3Config config;

//    private final FileMetadataRepository metadataRepository;

    @PostConstruct
    public void init() {
        String bucket = config.getBucket();

        try {
            if (bucketExists(bucket)) {
                log.info("Bucket '{}' already exists", bucket);
                return;
            }
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucket)
                    .build());
            log.info("Bucket '{}' created successfully", bucket);
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

        log.info("S3FileStorageService initialization completed");
    }

    // TODO использовать eTag для контроля целостности данных
    public String uploadFile(MultipartFile file, String ownerId) {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        log.info("Starting file upload: name='{}', size={}, ownerId={}",
                originalFilename, fileSize, ownerId);

        String key = generateKey(ownerId, originalFilename);
        log.debug("Generated S3 key: '{}' for file: '{}'", key, originalFilename);

        try (InputStream input = file.getInputStream()) {
            // Todo Понадобиться ли кеш для проверки дубликатов
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] fileBytes = file.getBytes();
//            byte[] md5Bytes = md.digest(fileBytes);
//            String contentMd5 = Base64.getEncoder().encodeToString(md5Bytes);

            long startTime = System.currentTimeMillis();

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(config.getBucket())
                            .key(key)
                            .contentType(contentType)
//                            .contentMD5(contentMd5)
                            .build(),
                    RequestBody.fromInputStream(input, fileSize));

//            FileMetadata metadata = FileMetadata.builder()
//                    .storageKey(key)
//                    .originalName(originalFilename)
//                    .contentType(contentType)
//                    .size(fileSize)
//                    .ownerId(ownerId)
//                    .uploadedAt(Instant.now())
//                    .build();
//
//            metadataRepository.save(metadata);

            log.info("File uploaded successfully: key='{}', duration={}ms",
                    key, System.currentTimeMillis() - startTime);
            return key;
        } catch (Exception e) {
            log.error("Error uploading file: name='{}', key='{}'",
                    originalFilename, key, e);
            throw new FileUploadException(key, ownerId, e);
        }
    }

//    @Override
//    public DownloadedFile downloadFile(String key) {
//        FileMetadata metadata = metadataRepository.findByStorageKey(key)
//                .orElseThrow(() -> new FileNotFoundException(key));
//
//        try (InputStream s3Object = s3Client.getObject(GetObjectRequest.builder()
//                .bucket(config.getBucket())
//                .key(key)
//                .build())) {
//
//            byte[] bytes = s3Object.readAllBytes();
//
//            return new DownloadedFile(
//                    bytes,
//                    metadata.getOriginalName(),
//                    metadata.getContentType(),
//                    metadata.getSize(),
//                    metadata.getOwnerId(),
//                    metadata.getUploadedAt()
//            );
//        } catch (NoSuchKeyException e) {
//            log.warn("File not found: key='{}'", key);
//            throw new FileNotFoundException(key, e);
//        } catch (Exception e) {
//            log.error("Error downloading file: key='{}'", key, e);
//            throw new FileDownloadException(key, e);
//        }
//    }
//
//    @Override
//    public void deleteFile(String key) {
//        try {
//            s3Client.deleteObject(DeleteObjectRequest.builder()
//                    .bucket(config.getBucket())
//                    .key(key)
//                    .build());
//            int deleted = metadataRepository.deleteByStorageKey(key);
//            if (deleted == 0) {
//                log.warn("No metadata found for key '{}'", key);
//            }
//
//            log.info("File '{}' deleted successfully from S3 and metadata removed", key);
//        } catch (Exception e) {
//            throw new FileDeleteException(key, e);
//        }
//    }

    private boolean bucketExists(String bucket) {
        try {
            s3Client.headBucket(b -> b.bucket(bucket));
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }
}