package com.example.cloud.fileservice.service;


import com.example.cloud.fileservice.exception.StorageException;
import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FileManagementServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileMetadataService fileMetadataService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileManagementService fileManagementService;

    private final UUID ID = UUID.randomUUID();
    private final String OWNER_ID = "user-123";
    private final String HASH = "hash123";
    private final String STORAGE_KEY = "key123";
    private final MultipartFile FILE = mock(MultipartFile.class);

    @BeforeEach
    void setup() throws Exception {
        lenient().when(file.getOriginalFilename()).thenReturn("test.txt");
        lenient().when(file.getContentType()).thenReturn("text/plain");
        lenient().when(file.getSize()).thenReturn(100L);
        lenient().when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
    }

    @Test
    void uploadFile_success() {
        UUID fileId = UUID.randomUUID();

        try (MockedStatic<HashUtil> mockedHash = mockStatic(HashUtil.class)) {
            mockedHash.when(() -> HashUtil.sha256(any(InputStream.class))).thenReturn(HASH);

            when(fileStorageService.uploadFile(any(), any(), any(), anyLong(), any()))
                    .thenReturn(STORAGE_KEY);

            FileMetadata metadata = FileMetadata.builder()
                    .id(fileId)
                    .build();

            when(fileMetadataService.saveMetadata(any(), any(), any(), anyLong(), any(), any()))
                    .thenReturn(metadata);

            UUID result = fileManagementService.uploadFile(file, OWNER_ID);

            assertEquals(fileId, result);

            verify(fileStorageService, times(1))
                    .uploadFile(any(), any(), any(), anyLong(), eq(OWNER_ID));

            verify(fileMetadataService, times(1))
                    .saveMetadata(any(), any(), any(), anyLong(), eq(OWNER_ID), eq(HASH));
        }
    }

    @Test
    void uploadFile_hashCalculationFails() throws Exception {
        when(file.getInputStream()).thenThrow(new RuntimeException("IO error"));

        assertThrows(StorageException.class, () ->
                fileManagementService.uploadFile(file, OWNER_ID)
        );

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(fileMetadataService);
    }

    @Test
    void uploadFile_storageFails() {
        try (MockedStatic<HashUtil> mockedHash = mockStatic(HashUtil.class)) {
            mockedHash.when(() -> HashUtil.sha256(any(InputStream.class))).thenReturn(HASH);

            when(fileStorageService.uploadFile(any(), any(), any(), anyLong(), any()))
                    .thenThrow(new RuntimeException("S3 error"));

            assertThrows(RuntimeException.class, () ->
                    fileManagementService.uploadFile(file, OWNER_ID)
            );

            verify(fileStorageService, times(1)).uploadFile(any(), any(), any(), anyLong(), any());
            verifyNoInteractions(fileMetadataService);
        }
    }

    @Test
    void uploadFile_metadataSaveFails() {
        try (MockedStatic<HashUtil> mockedHash = mockStatic(HashUtil.class)) {
            mockedHash.when(() -> HashUtil.sha256(any(InputStream.class))).thenReturn(HASH);

            when(fileStorageService.uploadFile(any(), any(), any(), anyLong(), any()))
                    .thenReturn(STORAGE_KEY);

            when(fileMetadataService.saveMetadata(any(), any(), any(), anyLong(), any(), any()))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () ->
                    fileManagementService.uploadFile(file, OWNER_ID)
            );

            verify(fileStorageService, times(1))
                    .uploadFile(any(), any(), any(), anyLong(), any());

            verify(fileMetadataService, times(1))
                    .saveMetadata(any(), any(), any(), anyLong(), any(), any());
        }
    }

    @Test
    void uploadFile_metadataFails_shouldRollbackStorage() {
        try (MockedStatic<HashUtil> mockedHash = mockStatic(HashUtil.class)) {
            mockedHash.when(() -> HashUtil.sha256(any(InputStream.class))).thenReturn(HASH);

            when(fileStorageService.uploadFile(any(), any(), any(), anyLong(), any()))
                    .thenReturn(STORAGE_KEY);

            when(fileMetadataService.saveMetadata(any(), any(), any(), anyLong(), any(), any()))
                    .thenThrow(new RuntimeException("DB error"));

            doNothing().when(fileStorageService).deleteFile(STORAGE_KEY);

            assertThrows(RuntimeException.class, () ->
                    fileManagementService.uploadFile(file, OWNER_ID)
            );

            verify(fileStorageService).deleteFile(STORAGE_KEY);
        }
    }

    @Test
    void uploadFile_shouldThrowException_whenOwnerIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> fileManagementService.uploadFile(FILE, null)
        );

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(fileMetadataService);
    }

    @Test
    void uploadFile_shouldThrowException_whenOwnerIdIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> fileManagementService.uploadFile(FILE, "   ")
        );

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(fileMetadataService);
    }

    @Test
    void deleteFile_shouldCallService_whenInputIsValid() {
        fileManagementService.deleteFile(ID, OWNER_ID);

        verify(fileMetadataService)
                .markAsDeleted(ID, OWNER_ID);
    }

    @Test
    void deleteFile_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.deleteFile(null, OWNER_ID));

        verifyNoInteractions(fileMetadataService);
    }

    @Test
    void deleteFile_shouldThrowException_whenOwnerIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.deleteFile(ID, null));

        verifyNoInteractions(fileMetadataService);
    }

    @Test
    void deleteFile_shouldThrowException_whenOwnerIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.deleteFile(ID, " "));

        verifyNoInteractions(fileMetadataService);
    }
}