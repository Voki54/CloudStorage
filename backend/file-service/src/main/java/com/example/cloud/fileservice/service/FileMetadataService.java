package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.dto.FileDetailsDto;
import com.example.cloud.fileservice.dto.FileDownloadMetadataDto;
import com.example.cloud.fileservice.dto.FileMetadataDto;
import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.mapper.FileMetadataMapper;
import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataService {
    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadata saveMetadata(String storageKey, String originalFilename, String contentType, long fileSize,
                                     String ownerId, String fileHash, UUID directoryId) {

        return fileMetadataRepository.save(FileMetadata.builder()
                .storageKey(storageKey)
                .originalName(originalFilename)
                .contentType(contentType)
                .size(fileSize)
                .ownerId(ownerId)
                .hash(fileHash)
                .directoryId(directoryId)
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

    public List<FileMetadataDto> getFilesMetadataByDirectory(UUID directoryId, String ownerId) {
        return fileMetadataRepository.findByDirectoryIdAndOwnerIdAndIsDeletedFalse(directoryId, ownerId)
                .stream()
                .map(FileMetadataMapper::toDto)
                .toList();
    }

    public FileDownloadMetadataDto getFileMetadata(UUID id, String ownerId) {
        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new NotFoundException(id));

        return FileMetadataMapper.toDownloadDto(fileMetadata);
    }

    public FileDetailsDto getFileDetails(UUID id, String ownerId) {
        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new NotFoundException(id));

        return FileMetadataMapper.toDetailsDto(fileMetadata);
    }
}
