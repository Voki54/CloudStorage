package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.dto.FileDownloadMetadataDto;
import com.example.cloud.fileservice.dto.FileMetadataDto;
import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.mapper.FileMetadataMapper;
import com.example.cloud.fileservice.model.FileMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import com.example.cloud.fileservice.repository.FileMetadataRepository;

import java.util.List;
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

    public List<FileMetadataDto> getMetadataForAllUserFiles(String ownerId) {
        return fileMetadataRepository.findAllByOwnerIdAndIsDeletedFalse(ownerId)
                .stream()
                .map(FileMetadataMapper::toDto)
                .toList();
    }

    public FileDownloadMetadataDto getFileMetadata (UUID id, String ownerId) {
        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new NotFoundException(id));

        return FileMetadataMapper.toDownloadDto(fileMetadata);
    }
}
