package com.example.cloud.directoryservice.dto;

import java.util.UUID;

public record CreateDirectoryResponse(
        UUID id, String name, UUID parentId
) {
}