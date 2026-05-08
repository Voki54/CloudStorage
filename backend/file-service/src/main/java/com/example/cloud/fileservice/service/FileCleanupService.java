package com.example.cloud.fileservice.service;

import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileCleanupService {

    private final FileMetadataRepository repository;
    private final FileStorageService fileStorageService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteSingleFile(FileMetadata file) {
        fileStorageService.deleteFile(file.getStorageKey());
        repository.delete(file);
    }
}