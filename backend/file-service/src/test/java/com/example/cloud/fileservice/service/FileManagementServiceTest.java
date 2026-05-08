package com.example.cloud.fileservice.service;


import com.example.cloud.fileservice.dto.FileDetailsDto;
import com.example.cloud.fileservice.dto.FileDownloadData;
import com.example.cloud.fileservice.exception.FileDownloadException;
import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.exception.StorageException;
import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.repository.FileMetadataRepository;
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
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FileManagementServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileManagementService fileManagementService;

    private final UUID ID = UUID.randomUUID();
    private final String OWNER_ID = "user-123";
    private final String HASH = "hash123";
    private final String STORAGE_KEY = "key123";
    private final String ORIGINAL_FILENAME = "file.txt";
    private final String CONTENT_TYPE = "text/plain";
    private final long FILE_SIZE = 100L;
    private final UUID DIR_ID = UUID.randomUUID();
    private final MultipartFile FILE = mock(MultipartFile.class);

    private final FileMetadata METADATA = FileMetadata.builder()
            .id(ID)
            .storageKey(STORAGE_KEY)
            .originalName(ORIGINAL_FILENAME)
            .contentType(CONTENT_TYPE)
            .size(FILE_SIZE)
            .ownerId(OWNER_ID)
            .hash("hash123")
            .directoryId(DIR_ID)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

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

            when(fileMetadataRepository.save(any())).thenReturn(metadata);

            UUID result = fileManagementService.uploadFile(file, OWNER_ID, DIR_ID);

            assertEquals(fileId, result);

            verify(fileStorageService, times(1))
                    .uploadFile(any(), any(), any(), anyLong(), eq(OWNER_ID));

            verify(fileMetadataRepository, times(1)).save(any());
        }
    }

    @Test
    void uploadFile_hashCalculationFails() throws Exception {
        when(file.getInputStream()).thenThrow(new RuntimeException("IO error"));

        assertThrows(StorageException.class, () ->
                fileManagementService.uploadFile(file, OWNER_ID, DIR_ID)
        );

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(fileMetadataRepository);
    }

    @Test
    void uploadFile_storageFails() {
        try (MockedStatic<HashUtil> mockedHash = mockStatic(HashUtil.class)) {
            mockedHash.when(() -> HashUtil.sha256(any(InputStream.class))).thenReturn(HASH);

            when(fileStorageService.uploadFile(any(), any(), any(), anyLong(), any()))
                    .thenThrow(new RuntimeException("S3 error"));

            assertThrows(RuntimeException.class, () ->
                    fileManagementService.uploadFile(file, OWNER_ID, DIR_ID)
            );

            verify(fileStorageService, times(1)).uploadFile(any(), any(), any(), anyLong(), any());
            verifyNoInteractions(fileMetadataRepository);
        }
    }

    @Test
    void uploadFile_metadataSaveFails() {
        try (MockedStatic<HashUtil> mockedHash = mockStatic(HashUtil.class)) {
            mockedHash.when(() -> HashUtil.sha256(any(InputStream.class))).thenReturn(HASH);

            when(fileStorageService.uploadFile(any(), any(), any(), anyLong(), any()))
                    .thenReturn(STORAGE_KEY);

            when(fileMetadataRepository.save(any())).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () ->
                    fileManagementService.uploadFile(file, OWNER_ID, DIR_ID)
            );

            verify(fileStorageService, times(1))
                    .uploadFile(any(), any(), any(), anyLong(), any());

            verify(fileMetadataRepository, times(1)).save(any());
        }
    }

    @Test
    void uploadFile_metadataFails_shouldRollbackStorage() {
        try (MockedStatic<HashUtil> mockedHash = mockStatic(HashUtil.class)) {
            mockedHash.when(() -> HashUtil.sha256(any(InputStream.class))).thenReturn(HASH);

            when(fileStorageService.uploadFile(any(), any(), any(), anyLong(), any()))
                    .thenReturn(STORAGE_KEY);

            when(fileMetadataRepository.save(any())).thenThrow(new RuntimeException("DB error"));

            doNothing().when(fileStorageService).deleteFile(STORAGE_KEY);

            assertThrows(RuntimeException.class, () ->
                    fileManagementService.uploadFile(file, OWNER_ID, DIR_ID)
            );

            verify(fileStorageService).deleteFile(STORAGE_KEY);
        }
    }

    @Test
    void uploadFile_shouldThrowException_whenOwnerIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> fileManagementService.uploadFile(FILE, null, DIR_ID)
        );

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(fileMetadataRepository);
    }

    @Test
    void uploadFile_shouldThrowException_whenDirectoryIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> fileManagementService.uploadFile(FILE, OWNER_ID, null)
        );

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(fileMetadataRepository);
    }

    @Test
    void uploadFile_shouldThrowException_whenOwnerIdIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> fileManagementService.uploadFile(FILE, "   ", DIR_ID)
        );

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(fileMetadataRepository);
    }

    @Test
    void deleteFile_shouldCallService_whenInputIsValid() {
        when(fileMetadataRepository.markAsDeletedByIdAndOwnerId(any(), any())).thenReturn(1);
        fileManagementService.deleteFile(ID, OWNER_ID);
        verify(fileMetadataRepository).markAsDeletedByIdAndOwnerId(ID, OWNER_ID);
    }

    @Test
    void deleteFile_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.deleteFile(null, OWNER_ID));

        verifyNoInteractions(fileMetadataRepository);
    }

    @Test
    void deleteFile_shouldThrowException_whenOwnerIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.deleteFile(ID, null));

        verifyNoInteractions(fileMetadataRepository);
    }

    @Test
    void deleteFile_shouldThrowException_whenOwnerIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.deleteFile(ID, " "));

        verifyNoInteractions(fileMetadataRepository);
    }

    @Test
    void deleteFile_shouldThrowException_whenFileNotFound() {
        when(fileMetadataRepository.markAsDeletedByIdAndOwnerId(ID, OWNER_ID))
                .thenReturn(0);

        assertThrows(NotFoundException.class,
                () -> fileManagementService.deleteFile(ID, OWNER_ID));

        verify(fileMetadataRepository)
                .markAsDeletedByIdAndOwnerId(ID, OWNER_ID);
    }

    @Test
    void downloadFile_shouldDownloadFileSuccessfully() {
        InputStream mockStream = new ByteArrayInputStream("data".getBytes());

        when(fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.of(METADATA));

        when(fileStorageService.getFileStream(STORAGE_KEY)).thenReturn(mockStream);

        FileDownloadData result = fileManagementService.downloadFile(ID, OWNER_ID);

        assertNotNull(result);
        assertEquals(ORIGINAL_FILENAME, result.originalName());
        assertEquals(CONTENT_TYPE, result.contentType());
        assertEquals(FILE_SIZE, result.size());
        assertEquals(mockStream, result.data());

        verify(fileMetadataRepository).findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID);
        verify(fileStorageService).getFileStream(STORAGE_KEY);
    }

    @Test
    void downloadFile_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.downloadFile(null, OWNER_ID));
    }

    @Test
    void downloadFile_shouldThrowException_whenOwnerIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.downloadFile(ID, null));
    }

    @Test
    void downloadFile_shouldThrowException_whenOwnerIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.downloadFile(ID, " "));
    }

    @Test
    void downloadFile_shouldPropagateException_whenMetadataNotFound() {
        when(fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class,
                () -> fileManagementService.downloadFile(ID, OWNER_ID));

        verify(fileStorageService, never()).getFileStream(any());
    }

    @Test
    void downloadFile_shouldPropagateException_whenStorageFails() {
        when(fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.of(METADATA));

        when(fileStorageService.getFileStream(STORAGE_KEY))
                .thenThrow(new FileDownloadException(STORAGE_KEY, new RuntimeException()));

        assertThrows(FileDownloadException.class,
                () -> fileManagementService.downloadFile(ID, OWNER_ID));
    }

    @Test
    void getFileDetails_success() {
        FileDetailsDto dto = new FileDetailsDto(
                ID,
                ORIGINAL_FILENAME,
                CONTENT_TYPE,
                FILE_SIZE,
                METADATA.getCreatedAt(),
                METADATA.getUpdatedAt()
        );

        when(fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.of(METADATA));

        FileDetailsDto result = fileManagementService.getFileDetails(ID, OWNER_ID);

        assertEquals(dto, result);

        verify(fileMetadataRepository).findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID);
    }

    @Test
    void getFileDetails_shouldThrowIllegalArgumentException_whenInvalidParams() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.getFileDetails(null, OWNER_ID));

        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.getFileDetails(ID, null));

        assertThrows(IllegalArgumentException.class,
                () -> fileManagementService.getFileDetails(ID, ""));
    }

    @Test
    void getFileDetails_shouldThrowNotFoundException() {
        when(fileMetadataRepository
                .findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> fileManagementService.getFileDetails(ID, OWNER_ID));
    }
}