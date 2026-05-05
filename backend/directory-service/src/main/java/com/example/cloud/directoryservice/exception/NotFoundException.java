package com.example.cloud.directoryservice.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String key, Throwable cause) {
        super("Directory not found: key='" + key + "'", cause);
    }

    public NotFoundException(UUID id) {
        super("No directory found with id='" + id + "' and isDeleted=True");
    }
}
