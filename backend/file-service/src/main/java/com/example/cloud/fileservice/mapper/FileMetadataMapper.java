package com.example.cloud.fileservice.mapper;

import com.example.cloud.fileservice.dto.FileDetailsDto;
import com.example.cloud.fileservice.dto.FileDownloadMetadataDto;
import com.example.cloud.fileservice.dto.FileMetadataDto;
import com.example.cloud.fileservice.model.FileMetadata;

public class FileMetadataMapper {

    public static FileMetadataDto toDto(FileMetadata entity) {
        return new FileMetadataDto(
                entity.getId(),
                entity.getOriginalName(),
                entity.getContentType(),
                entity.getSize()
        );
    }

    public static FileDownloadMetadataDto toDownloadDto(FileMetadata entity) {
        return new FileDownloadMetadataDto(
                entity.getStorageKey(),
                entity.getOriginalName(),
                entity.getContentType(),
                entity.getSize()
        );
    }

    public static FileDetailsDto toDetailsDto(FileMetadata entity) {
        return new FileDetailsDto(
                entity.getId(),
                entity.getOriginalName(),
                entity.getContentType(),
                entity.getSize(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
