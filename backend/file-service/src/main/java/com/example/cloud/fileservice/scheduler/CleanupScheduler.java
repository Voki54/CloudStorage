package com.example.cloud.fileservice.scheduler;

import com.example.cloud.fileservice.exception.FileDeleteException;
import com.example.cloud.fileservice.exception.NotFoundException;
import com.example.cloud.fileservice.model.FileMetadata;
import com.example.cloud.fileservice.service.FileCleanupService;
import com.example.cloud.fileservice.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.example.cloud.fileservice.repository.FileMetadataRepository;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanupScheduler {

    private final FileMetadataRepository repository;
    private final FileCleanupService cleanupService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldDeletedFiles() {
        log.info("Starting cleanup of deleted files");

        List<FileMetadata> files = repository.findAllByIsDeletedTrue();

        int successCount = 0;
        int failedCount = 0;

        for (FileMetadata file : files) {
            try {
                cleanupService.deleteSingleFile(file);
                successCount++;
            } catch (FileDeleteException e) {
                failedCount++;

                log.error(
                        "Failed to delete file from storage. fileId={}, storageKey={}",
                        file.getId(),
                        file.getStorageKey(),
                        e
                );

            } catch (Exception e) {
                failedCount++;

                log.error(
                        "Unexpected error during cleanup. fileId={}, storageKey={}",
                        file.getId(),
                        file.getStorageKey(),
                        e
                );
            }
        }

        log.info("Cleanup completed. total={}, success={}, failed={}", files.size(), successCount, failedCount);
    }
}
