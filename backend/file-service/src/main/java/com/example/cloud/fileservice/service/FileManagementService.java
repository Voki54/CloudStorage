package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.dto.FileDownloadData;
import com.example.cloud.fileservice.dto.FileDownloadMetadataDto;
import com.example.cloud.fileservice.dto.FileMetadataDto;
import com.example.cloud.fileservice.exception.StorageException;
import com.example.cloud.fileservice.model.FileMetadata;
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
    private final FileMetadataService fileMetadataService;


    @Transactional
    public UUID uploadFile(MultipartFile file, String ownerId) {
        if (ownerId == null || ownerId.isBlank()) {
            log.error("The ownerId is incorrect");
            throw new IllegalArgumentException("The ownerId is incorrect");
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

            FileMetadata fileMetadata = fileMetadataService.saveMetadata(
                    storageKey,
                    originalFilename,
                    contentType,
                    fileSize,
                    ownerId,
                    fileHash
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

        fileMetadataService.markAsDeleted(id, ownerId);
    }

    @Transactional(readOnly = true)
    public List<FileMetadataDto> getMetadataForAllUserFiles(String ownerId) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("ownerId is invalid");
        }

        return fileMetadataService.getMetadataForAllUserFiles(ownerId);
    }

    @Transactional(readOnly = true)
    public FileDownloadData downloadFile(UUID id, String ownerId) {
        if (id == null || ownerId == null || ownerId.isBlank()) {
            log.error("The id or ownerId is incorrect");
            throw new IllegalArgumentException("The id or ownerId is incorrect");
        }
//TODO поправить fileMetadataDto - убрать лишние поля
        FileDownloadMetadataDto fileMetadataDto = fileMetadataService.getFileMetadata(id, ownerId);

        InputStream fileStream = fileStorageService.getFileStream(fileMetadataDto.storageKey());

        return new FileDownloadData(
                fileStream,
                fileMetadataDto.originalName(),
                fileMetadataDto.contentType(),
                fileMetadataDto.size()
        );
    }
}
