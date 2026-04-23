package com.example.cloud.fileservice.dto;

import java.time.Instant;
import java.util.UUID;

public record FileMetadataDto(
        UUID id,
        String originalName,
        String contentType,
        long size,
        Instant createdAt,
        Instant updatedAt
) {
}
