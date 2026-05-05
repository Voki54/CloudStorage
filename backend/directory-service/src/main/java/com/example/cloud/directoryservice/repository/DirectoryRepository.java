package com.example.cloud.directoryservice.repository;

import com.example.cloud.directoryservice.model.DirectoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DirectoryRepository extends JpaRepository<DirectoryEntity, UUID> {

    Optional<DirectoryEntity> findByIdAndOwnerIdAndIsDeletedFalse(UUID id, String ownerId);

    Optional<DirectoryEntity> findByOwnerIdAndParentIdIsNullAndIsDeletedFalse(String ownerId);

    List<DirectoryEntity> findByParentIdAndOwnerIdAndIsDeletedFalse(UUID parentId, String ownerId);

    List<DirectoryEntity> findAllByOwnerIdAndIsDeletedFalse(String ownerId);

    boolean existsByIdAndOwnerIdAndIsDeletedFalse(UUID id, String ownerId);

    @Modifying
    @Query("""
        UPDATE DirectoryEntity d
        SET d.isDeleted = true, d.updatedAt = CURRENT_TIMESTAMP
        WHERE d.id = :id AND d.ownerId = :ownerId AND d.isDeleted = false
    """)
    void softDelete(UUID id, String ownerId);

    @Modifying
    @Query("""
        UPDATE DirectoryEntity d
        SET d.isDeleted = true, d.updatedAt = CURRENT_TIMESTAMP
        WHERE d.id IN :ids AND d.ownerId = :ownerId AND d.isDeleted = false
    """)
    void softDeleteAll(List<UUID> ids, String ownerId);
}