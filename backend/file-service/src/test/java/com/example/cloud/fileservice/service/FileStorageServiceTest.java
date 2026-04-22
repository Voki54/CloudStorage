package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.config.S3Config;
import com.example.cloud.fileservice.exception.FileDeleteException;
import com.example.cloud.fileservice.exception.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {
    @Mock
    private S3Client s3Client;

    @Mock
    private S3Config config;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileStorageService fileStorageService;

    private static final String OWNER_ID = "user-1";
    private static final String FILENAME = "file.txt";
    private static final String CONTENT_TYPE = "text/plain";
    private static final long SIZE = 100L;
    private static final byte[] CONTENT_FILE = "cloud_storage".getBytes();
    private static final String STORAGE_KEY = "file123";

    @BeforeEach
    void setup() {
        lenient().when(config.getBucket()).thenReturn("test-bucket");
    }

    @Test
    void uploadFile_success() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(CONTENT_FILE);
        when(file.getInputStream()).thenReturn(inputStream);

        PutObjectResponse mockResponse = PutObjectResponse.builder()
                .eTag("some-etag")
                .build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(mockResponse);

        String key = fileStorageService.uploadFile(file, FILENAME, CONTENT_TYPE, SIZE, OWNER_ID);

        assertNotNull(key);

        verify(s3Client, times(1)).putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class)
        );
    }

    @Test
    void uploadFile_s3ThrowsException_shouldThrowFileUploadException() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(CONTENT_FILE);

        when(file.getInputStream()).thenReturn(inputStream);

        doThrow(new RuntimeException("S3 error"))
                .when(s3Client)
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        FileUploadException ex = assertThrows(
                FileUploadException.class,
                () -> fileStorageService.uploadFile(file, FILENAME, CONTENT_TYPE, SIZE, OWNER_ID)
        );

        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains(OWNER_ID));
    }

    @Test
    void uploadFile_inputStreamThrows_shouldThrowFileUploadException() throws Exception {
        when(file.getInputStream()).thenThrow(new IOException("Stream error"));

        FileUploadException ex = assertThrows(
                FileUploadException.class,
                () -> fileStorageService.uploadFile(file, FILENAME, CONTENT_TYPE, SIZE, OWNER_ID)
        );

        assertNotNull(ex);
    }

    @Test
    void deleteFile_success() {
        fileStorageService.deleteFile(STORAGE_KEY);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_nullKey_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.deleteFile(null));

        verifyNoInteractions(s3Client);
    }

    @Test
    void deleteFile_s3Exception_shouldThrowFileDeleteException() {
        when(config.getBucket()).thenReturn("test-bucket");

        doThrow(S3Exception.builder().message("S3 error").build())
                .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(FileDeleteException.class,
                () -> fileStorageService.deleteFile(STORAGE_KEY));

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
