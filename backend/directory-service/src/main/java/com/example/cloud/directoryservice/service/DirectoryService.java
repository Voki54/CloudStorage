package com.example.cloud.directoryservice.service;

import com.example.cloud.directoryservice.dto.CreateDirectoryRequest;
import com.example.cloud.directoryservice.exception.NotFoundException;
import com.example.cloud.directoryservice.model.DirectoryEntity;
import com.example.cloud.directoryservice.repository.DirectoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final DirectoryRepository directoryRepository;

    @Transactional
    public DirectoryEntity create(CreateDirectoryRequest request, String ownerId) {
        String name = request.name();
        UUID parentId = request.parentId();

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Directory name is empty");
        }

        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("OwnerId is invalid");
        }

        if (parentId != null) {
            boolean exists = directoryRepository.existsByIdAndOwnerIdAndIsDeletedFalse(parentId, ownerId);
            if (!exists) {
                throw new NotFoundException(parentId);
            }
        }

        return directoryRepository.save(DirectoryEntity.builder()
                .name(name)
                .ownerId(ownerId)
                .parentId(parentId)
                .build());
    }

    @Transactional
    public DirectoryEntity getOrCreateRoot(String ownerId) {
        return directoryRepository
                .findByOwnerIdAndParentIdIsNullAndIsDeletedFalse(ownerId)
                .orElseGet(() -> create(new CreateDirectoryRequest("root", null), ownerId));
    }

    @Transactional
    public List<DirectoryEntity> getChildren(UUID parentId, String ownerId) {
        if (parentId == null) {
            throw new IllegalArgumentException("parentId is invalid");
        }

        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("OwnerId is invalid");
        }

        boolean exists = directoryRepository.existsByIdAndOwnerIdAndIsDeletedFalse(parentId, ownerId);
        if (!exists) {
            throw new NotFoundException(parentId);
        }

        return directoryRepository.findByParentIdAndOwnerIdAndIsDeletedFalse(parentId, ownerId);
    }

    @Transactional
    public void delete(UUID id, String ownerId) {

        DirectoryEntity directory = directoryRepository
                .findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new NotFoundException(id));

        List<UUID> allIds = new ArrayList<>();
        collectChildrenIds(id, ownerId, allIds);

        allIds.add(id);

        directoryRepository.softDeleteAll(allIds, ownerId);
    }

    private void collectChildrenIds(UUID parentId, String ownerId, List<UUID> result) {
        List<DirectoryEntity> children = directoryRepository.findByParentIdAndOwnerIdAndIsDeletedFalse(parentId, ownerId);

        for (DirectoryEntity child : children) {
            result.add(child.getId());
            collectChildrenIds(child.getId(), ownerId, result);
        }
    }
}