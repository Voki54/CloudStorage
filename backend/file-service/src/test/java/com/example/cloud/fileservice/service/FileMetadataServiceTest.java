package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.repository.FileMetadataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private final String STORAGE_KEY = "user1/file.txt";
    private final String ORIGINAL_FILENAME = "file.txt";
    private final String CONTENT_TYPE = "text/plain";
    private final long FILE_SIZE = 100L;
    private final String OWNER_ID = "user1";
    private final String FILE_HASH = "hash123";

    @Test
    void saveMetadata_shouldSaveAndReturnMetadata() {
        FileMetadata savedMetadata = FileMetadata.builder()
                .storageKey(STORAGE_KEY)
                .originalName(ORIGINAL_FILENAME)
                .contentType(CONTENT_TYPE)
                .size(FILE_SIZE)
                .ownerId(OWNER_ID)
                .hash(FILE_HASH)
                .build();

        when(fileMetadataRepository.save(any(FileMetadata.class)))
                .thenReturn(savedMetadata);

        FileMetadata result = fileMetadataService.saveMetadata(
                STORAGE_KEY,
                ORIGINAL_FILENAME,
                CONTENT_TYPE,
                FILE_SIZE,
                OWNER_ID,
                FILE_HASH
        );

        assertNotNull(result);
        assertEquals(STORAGE_KEY, result.getStorageKey());
        assertEquals(ORIGINAL_FILENAME, result.getOriginalName());
        assertEquals(CONTENT_TYPE, result.getContentType());
        assertEquals(FILE_SIZE, result.getSize());
        assertEquals(OWNER_ID, result.getOwnerId());
        assertEquals(FILE_HASH, result.getHash());

        verify(fileMetadataRepository, times(1)).save(any(FileMetadata.class));
    }

//    @Test
//    void saveMetadata_shouldThrowException_whenStorageKeyIsNull() {
//        assertThrows(IllegalArgumentException.class, () ->
//                fileMetadataService.saveMetadata(
//                        null,
//                        "ORIGINAL_FILENAME",
//                        CONTENT_TYPE,
//                        FILE_SIZE,
//                        OWNER_ID,
//                        FILE_HASH
//                )
//        );
//
//        verify(fileMetadataRepository, never()).save(any());
//    }
//
//    @Test
//    void saveMetadata_shouldThrowException_whenOwnerIdIsNull() {
//        assertThrows(IllegalArgumentException.class, () ->
//                fileMetadataService.saveMetadata(
//                        STORAGE_KEY,
//                        ORIGINAL_FILENAME,
//                        CONTENT_TYPE,
//                        FILE_SIZE,
//                        null,
//                        FILE_HASH
//                )
//        );
//
//        verify(fileMetadataRepository, never()).save(any());
//    }

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
}
