package com.example.cloud.directoryservice.dto;

import java.util.UUID;

public record DirectoryDto(
        UUID id, String name
) {
}