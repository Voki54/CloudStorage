package com.example.cloud.fileservice.exception;

import java.util.UUID;

public class NotFoundException extends StorageException {

    public NotFoundException(String key) {
        super("File not found: key='" + key + "'");
    }

    public NotFoundException(String key, Throwable cause) {
        super("File not found: key='" + key + "'", cause);
    }

    public NotFoundException(UUID fileId) {
        super("No file metadata found with id='" + fileId + "' and isDeleted=True");
    }
}
