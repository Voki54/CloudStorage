package com.example.cloud.directoryservice.dto;

import java.util.UUID;

public record CreateDirectoryRequest(
        String name, UUID parentId
) {
}
