package com.example.cloud.fileservice.exception;

public class FileDeleteException extends StorageException {
    public FileDeleteException(String key, Throwable cause) {
        super("Failed to delete file with key: " + key, cause);
    }
}
