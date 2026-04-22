package com.example.cloud.fileservice.repository;

import com.example.cloud.fileservice.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
    Optional<FileMetadata> findByStorageKeyAndIsDeletedFalse(String storageKey);
    List<FileMetadata> findAllByOwnerIdAndIsDeletedFalse(String ownerId);
    Optional<FileMetadata> findByIdAndOwnerIdAndIsDeletedFalse(UUID id, String ownerId);
    Optional<FileMetadata> findByHashAndOwnerIdAndIsDeletedFalse(String hash, String ownerId);
    List<FileMetadata> findAllByIsDeletedTrue();
    @Modifying
    @Query("UPDATE FileMetadata f SET f.isDeleted = true WHERE f.id = :id AND f.ownerId = :ownerId " +
            "AND f.isDeleted = false")
    int markAsDeletedByIdAndOwnerId(@Param("id") UUID id, @Param("ownerId") String ownerId);
//    int deleteByStorageKey(String storageKey);
}
