package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.dto.FileDetailsDto;
import com.example.cloud.fileservice.dto.FileDownloadData;
import com.example.cloud.fileservice.dto.FileMetadataDto;
import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.exception.StorageException;
import com.example.cloud.fileservice.mapper.FileMetadataMapper;
import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.repository.FileMetadataRepository;
import com.example.cloud.fileservice.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final FileStorageService fileStorageService;
    private final FileMetadataRepository fileMetadataRepository;


    @Transactional
    public UUID uploadFile(MultipartFile file, String ownerId, UUID directoryId) {
        if (ownerId == null || ownerId.isBlank()) {
            log.error("The ownerId is incorrect");
            throw new IllegalArgumentException("The ownerId is incorrect");
        }

        if (directoryId == null) {
            throw new IllegalArgumentException("The directoryId is incorrect");
        }

        String fileHash;
        try (InputStream input = file.getInputStream()) {
            fileHash = HashUtil.sha256(input);
        } catch (Exception e) {
            log.error("Error calculating the hash of the file.", e);
            throw new StorageException("Failed to calculate file hash", e);
        }

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        String storageKey = null;

        try {
            storageKey = fileStorageService.uploadFile(file, originalFilename, contentType, fileSize, ownerId);

            if (storageKey == null || storageKey.isBlank()) {
                log.error("The storageKey and ownerId is incorrect");
                throw new IllegalArgumentException("The storageKey and ownerId is incorrect");
            }

            FileMetadata fileMetadata = fileMetadataRepository.save(FileMetadata.builder()
                    .storageKey(storageKey)
                    .originalName(originalFilename)
                    .contentType(contentType)
                    .size(fileSize)
                    .ownerId(ownerId)
                    .hash(fileHash)
                    .directoryId(directoryId)
                    .build()
            );

            return fileMetadata.getId();

        } catch (Exception e) {
            if (storageKey != null) {
                try {
                    fileStorageService.deleteFile(storageKey);
                } catch (Exception deleteEx) {
                    log.error("Failed to rollback file in storage", deleteEx);
                }
            }

            log.error("Failed to upload file for ownerId={}", ownerId, e);
            throw e;
        }
    }

    @Transactional
    public void deleteFile(UUID id, String ownerId) {
        if (id == null || ownerId == null || ownerId.isBlank()) {
            log.error("The id or ownerId is incorrect");
            throw new IllegalArgumentException("The id or ownerId is incorrect");
        }

        int updated = fileMetadataRepository.markAsDeletedByIdAndOwnerId(id, ownerId);

        if (updated == 0) {
            log.error("File not found, already deleted, or access denied");
            throw new NotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    public List<FileMetadataDto> getFilesMetadataByDirectory(UUID directoryId, String ownerId) {
        if (directoryId == null || ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("ownerId is invalid");
        }

        return fileMetadataRepository.findByDirectoryIdAndOwnerIdAndIsDeletedFalse(directoryId, ownerId)
                .stream()
                .map(FileMetadataMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public FileDetailsDto getFileDetails(UUID id, String ownerId) {
        if (id == null || ownerId == null || ownerId.isBlank()) {
            log.error("The id or ownerId is incorrect");
            throw new IllegalArgumentException("The id or ownerId is incorrect");
        }

        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new NotFoundException(id));

        return FileMetadataMapper.toDetailsDto(fileMetadata);
    }

    @Transactional(readOnly = true)
    public FileDownloadData downloadFile(UUID id, String ownerId) {
        if (id == null || ownerId == null || ownerId.isBlank()) {
            log.error("The id or ownerId is incorrect");
            throw new IllegalArgumentException("The id or ownerId is incorrect");
        }

        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new NotFoundException(id));

        InputStream fileStream = fileStorageService.getFileStream(fileMetadata.getStorageKey());

        return new FileDownloadData(
                fileStream,
                fileMetadata.getOriginalName(),
                fileMetadata.getContentType(),
                fileMetadata.getSize()
        );
    }
}
