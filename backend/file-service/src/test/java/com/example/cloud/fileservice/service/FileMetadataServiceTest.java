package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.dto.FileDetailsDto;
import com.example.cloud.fileservice.dto.FileDownloadMetadataDto;
import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.repository.FileMetadataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileMetadataServiceTest {

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    @InjectMocks
    private FileMetadataService fileMetadataService;

    private final UUID ID = UUID.randomUUID();
    private final UUID DIR_ID = UUID.randomUUID();
    private final String STORAGE_KEY = "user1/file.txt";
    private final String ORIGINAL_FILENAME = "file.txt";
    private final String CONTENT_TYPE = "text/plain";
    private final long FILE_SIZE = 100L;
    private final String OWNER_ID = "user1";

    @Test
    void saveMetadata_shouldSaveAndReturnMetadata() {
        String fileHash = "hash123";
        FileMetadata savedMetadata = FileMetadata.builder()
                .storageKey(STORAGE_KEY)
                .originalName(ORIGINAL_FILENAME)
                .contentType(CONTENT_TYPE)
                .size(FILE_SIZE)
                .ownerId(OWNER_ID)
                .hash(fileHash)
                .directoryId(DIR_ID)
                .build();

        when(fileMetadataRepository.save(any(FileMetadata.class)))
                .thenReturn(savedMetadata);

        FileMetadata result = fileMetadataService.saveMetadata(
                STORAGE_KEY,
                ORIGINAL_FILENAME,
                CONTENT_TYPE,
                FILE_SIZE,
                OWNER_ID,
                fileHash,
                DIR_ID
        );

        assertNotNull(result);
        assertEquals(STORAGE_KEY, result.getStorageKey());
        assertEquals(ORIGINAL_FILENAME, result.getOriginalName());
        assertEquals(CONTENT_TYPE, result.getContentType());
        assertEquals(FILE_SIZE, result.getSize());
        assertEquals(OWNER_ID, result.getOwnerId());
        assertEquals(fileHash, result.getHash());
        assertEquals(DIR_ID, result.getDirectoryId());

        verify(fileMetadataRepository, times(1)).save(any(FileMetadata.class));
    }

    @Test
    void markAsDeleted_shouldSucceed_whenFileExists() {
        when(fileMetadataRepository.markAsDeletedByIdAndOwnerId(ID, OWNER_ID))
                .thenReturn(1);

        fileMetadataService.markAsDeleted(ID, OWNER_ID);

        verify(fileMetadataRepository)
                .markAsDeletedByIdAndOwnerId(ID, OWNER_ID);
    }

    @Test
    void markAsDeleted_shouldThrowException_whenFileNotFound() {
        when(fileMetadataRepository.markAsDeletedByIdAndOwnerId(ID, OWNER_ID))
                .thenReturn(0);

        assertThrows(NotFoundException.class,
                () -> fileMetadataService.markAsDeleted(ID, OWNER_ID));

        verify(fileMetadataRepository)
                .markAsDeletedByIdAndOwnerId(ID, OWNER_ID);
    }

    @Test
    void shouldReturnMetadata_whenFileExists() {
        FileMetadata entity = new FileMetadata();
        entity.setOriginalName(ORIGINAL_FILENAME);
        entity.setContentType(CONTENT_TYPE);
        entity.setSize(FILE_SIZE);

        when(fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.of(entity));

        FileDownloadMetadataDto result = fileMetadataService.getFileMetadata(ID, OWNER_ID);

        assertNotNull(result);
        assertEquals(ORIGINAL_FILENAME, result.originalName());
        assertEquals(CONTENT_TYPE, result.contentType());
        assertEquals(FILE_SIZE, result.size());
    }

    @Test
    void shouldThrowNotFound_whenFileDoesNotExist() {
        when(fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> fileMetadataService.getFileMetadata(ID, OWNER_ID)
        );
    }

    @Test
    void getFileDetails_success() {
        FileMetadata metadata = FileMetadata.builder().id(ID).build();

        when(fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.of(metadata));

        FileDetailsDto result = fileMetadataService.getFileDetails(ID, OWNER_ID);

        assertNotNull(result);

        verify(fileMetadataRepository)
                .findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID);
    }

    @Test
    void getFileDetails_notFound() {
        when(fileMetadataRepository
                .findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> fileMetadataService.getFileDetails(ID, OWNER_ID));
    }
}
