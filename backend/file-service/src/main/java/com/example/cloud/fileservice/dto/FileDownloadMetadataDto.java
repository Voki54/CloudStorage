package com.example.cloud.fileservice.dto;

import java.time.Instant;

public record FileDownloadMetadataDto(
    String storageKey,
    String originalName,
    String contentType,
    long size
//    long size,
//    Instant createdAt,
//    Instant updatedAt
) {}
