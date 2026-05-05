package com.example.cloud.directoryservice.service;

import com.example.cloud.directoryservice.dto.CreateDirectoryRequest;
import com.example.cloud.directoryservice.exception.NotFoundException;
import com.example.cloud.directoryservice.model.DirectoryEntity;
import com.example.cloud.directoryservice.repository.DirectoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectoryServiceTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @InjectMocks
    private DirectoryService directoryService;

    private final UUID ID = UUID.randomUUID();
    private final String OWNER_ID = "user-1";
    private final String DIR_NAME = "docs";
    private final UUID PARENT_ID = UUID.randomUUID();

    @Test
    void create_shouldCreateDirectory_whenValidWithoutParent() {
        CreateDirectoryRequest request = new CreateDirectoryRequest(DIR_NAME, null);

        DirectoryEntity saved = DirectoryEntity.builder()
                .name(DIR_NAME)
                .ownerId(OWNER_ID)
                .parentId(null)
                .build();

        when(directoryRepository.save(any())).thenReturn(saved);

        DirectoryEntity result = directoryService.create(request, OWNER_ID);

        assertEquals(DIR_NAME, result.getName());
        assertEquals(OWNER_ID, result.getOwnerId());
        assertNull(result.getParentId());

        verify(directoryRepository).save(any());
    }

    @Test
    void create_shouldCreateDirectory_whenParentExists() {
        UUID parentId = UUID.randomUUID();
        CreateDirectoryRequest request = new CreateDirectoryRequest(DIR_NAME, parentId);

        when(directoryRepository.existsByIdAndOwnerIdAndIsDeletedFalse(parentId, OWNER_ID))
                .thenReturn(true);

        when(directoryRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DirectoryEntity result = directoryService.create(request, OWNER_ID);

        assertEquals(parentId, result.getParentId());

        verify(directoryRepository).existsByIdAndOwnerIdAndIsDeletedFalse(parentId, OWNER_ID);
        verify(directoryRepository).save(any());
    }

    @Test
    void create_shouldThrowException_whenNameIsBlank() {
        CreateDirectoryRequest request = new CreateDirectoryRequest(" ", null);

        assertThrows(IllegalArgumentException.class,
                () -> directoryService.create(request, OWNER_ID));
    }

    @Test
    void create_shouldThrowException_whenOwnerIdInvalid() {
        CreateDirectoryRequest request = new CreateDirectoryRequest(DIR_NAME, null);

        assertThrows(IllegalArgumentException.class,
                () -> directoryService.create(request, " "));
    }

    @Test
    void create_shouldThrowNotFound_whenParentNotExists() {
        CreateDirectoryRequest request = new CreateDirectoryRequest(DIR_NAME, PARENT_ID);

        when(directoryRepository.existsByIdAndOwnerIdAndIsDeletedFalse(PARENT_ID, OWNER_ID))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> directoryService.create(request, OWNER_ID));

        verify(directoryRepository).existsByIdAndOwnerIdAndIsDeletedFalse(PARENT_ID, OWNER_ID);
        verify(directoryRepository, never()).save(any());
    }

    @Test
    void getChildren_shouldReturnChildren_whenValid() {
        List<DirectoryEntity> expected = List.of(
                DirectoryEntity.builder().name("child1").build(),
                DirectoryEntity.builder().name("child2").build()
        );

        when(directoryRepository.existsByIdAndOwnerIdAndIsDeletedFalse(PARENT_ID, OWNER_ID))
                .thenReturn(true);

        when(directoryRepository.findByParentIdAndOwnerIdAndIsDeletedFalse(PARENT_ID, OWNER_ID))
                .thenReturn(expected);

        List<DirectoryEntity> result = directoryService.getChildren(PARENT_ID, OWNER_ID);

        assertEquals(2, result.size());

        verify(directoryRepository).findByParentIdAndOwnerIdAndIsDeletedFalse(PARENT_ID, OWNER_ID);
    }

    @Test
    void getChildren_shouldThrow_whenParentIdNull() {
        assertThrows(IllegalArgumentException.class,
                () -> directoryService.getChildren(null, OWNER_ID));
    }

    @Test
    void getChildren_shouldThrow_whenOwnerInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> directoryService.getChildren(PARENT_ID, " "));
    }

    @Test
    void getChildren_shouldThrow_whenParentNotFound() {
        when(directoryRepository.existsByIdAndOwnerIdAndIsDeletedFalse(PARENT_ID, OWNER_ID))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> directoryService.getChildren(PARENT_ID, OWNER_ID));

        verify(directoryRepository, never())
                .findByParentIdAndOwnerIdAndIsDeletedFalse(any(), any());
    }

    @Test
    void delete_shouldThrowNotFound_whenDirectoryNotExists() {
        when(directoryRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> directoryService.delete(ID, OWNER_ID));
    }

    @Test
    void delete_shouldDeleteOnlySelf_whenNoChildren() {
        when(directoryRepository.findByIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(Optional.of(new DirectoryEntity()));

        when(directoryRepository.findByParentIdAndOwnerIdAndIsDeletedFalse(ID, OWNER_ID))
                .thenReturn(List.of());

        directoryService.delete(ID, OWNER_ID);

        verify(directoryRepository).softDeleteAll(List.of(ID), OWNER_ID);
    }
    
    @Test
    void delete_shouldDeleteAllChildrenRecursively() {
        UUID root = ID;
        UUID child1 = UUID.randomUUID();
        UUID child2 = UUID.randomUUID();

        DirectoryEntity rootEntity = new DirectoryEntity();
        rootEntity.setId(root);

        DirectoryEntity childEntity1 = new DirectoryEntity();
        childEntity1.setId(child1);

        DirectoryEntity childEntity2 = new DirectoryEntity();
        childEntity2.setId(child2);

        when(directoryRepository.findByIdAndOwnerIdAndIsDeletedFalse(root, OWNER_ID))
                .thenReturn(Optional.of(rootEntity));

        when(directoryRepository.findByParentIdAndOwnerIdAndIsDeletedFalse(root, OWNER_ID))
                .thenReturn(List.of(childEntity1));

        when(directoryRepository.findByParentIdAndOwnerIdAndIsDeletedFalse(child1, OWNER_ID))
                .thenReturn(List.of(childEntity2));

        when(directoryRepository.findByParentIdAndOwnerIdAndIsDeletedFalse(child2, OWNER_ID))
                .thenReturn(List.of());

        directoryService.delete(root, OWNER_ID);

        ArgumentCaptor<List<UUID>> captor = ArgumentCaptor.forClass(List.class);
        verify(directoryRepository).softDeleteAll(captor.capture(), eq(OWNER_ID));

        List<UUID> deletedIds = captor.getValue();

        assertEquals(3, deletedIds.size());
        assertTrue(deletedIds.containsAll(List.of(root, child1, child2)));
    }
}
