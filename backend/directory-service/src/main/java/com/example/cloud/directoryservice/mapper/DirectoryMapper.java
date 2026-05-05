package com.example.cloud.directoryservice.mapper;


import com.example.cloud.directoryservice.dto.CreateDirectoryResponse;
import com.example.cloud.directoryservice.dto.DirectoryDto;
import com.example.cloud.directoryservice.model.DirectoryEntity;

public class DirectoryMapper {
    public static DirectoryDto toDto(DirectoryEntity entity) {
        return new DirectoryDto(
                entity.getId(),
                entity.getName()
        );
    }

    public static CreateDirectoryResponse toCreateResponse(DirectoryEntity entity) {
        return new CreateDirectoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getParentId()
        );
    }
}