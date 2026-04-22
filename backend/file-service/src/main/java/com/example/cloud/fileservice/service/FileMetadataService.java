package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.model.FileMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import com.example.cloud.fileservice.repository.FileMetadataRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataService {
    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadata saveMetadata(String storageKey, String originalFilename, String contentType, long fileSize,
                             String ownerId, String fileHash) {

        return fileMetadataRepository.save(FileMetadata.builder()
                .storageKey(storageKey)
                .originalName(originalFilename)
                .contentType(contentType)
                .size(fileSize)
                .ownerId(ownerId)
                .hash(fileHash)
                .build()
        );
    }

    public void markAsDeleted(UUID id, String ownerId) {
        int updated = fileMetadataRepository.markAsDeletedByIdAndOwnerId(id, ownerId);

        if (updated == 0) {
            log.error("File not found, already deleted, or access denied");
            throw new NotFoundException(id);
        }
    }
}
