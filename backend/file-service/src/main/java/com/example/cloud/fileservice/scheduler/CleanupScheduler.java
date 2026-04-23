package com.example.cloud.fileservice.scheduler;

import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.cloud.fileservice.repository.FileMetadataRepository;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanupScheduler {

    private final FileMetadataRepository repository;
    private final FileStorageService fileStorageService;

    @Scheduled(cron = "0 0 3 * * ?")
//    @Scheduled(cron = "*/30 * * * * ?")
    @Transactional
    public void cleanupOldDeletedFiles() {
        log.info("Starting cleanup of deleted files");

        List<FileMetadata> files = repository.findAllByIsDeletedTrue();

        for (FileMetadata file : files) {
            try {
                fileStorageService.deleteFile(file.getStorageKey());
                repository.delete(file);
                log.info("Deleted file: {}", file.getId());
            } catch (Exception e) {
                log.error("Failed to delete file: {}", file.getId(), e);
            }
        }

        log.info("Cleanup completed. Deleted {} files", files.size());
    }
}
