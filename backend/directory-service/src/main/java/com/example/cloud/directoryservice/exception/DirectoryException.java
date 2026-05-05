package com.example.cloud.directoryservice.exception;

public class DirectoryException extends RuntimeException {
    public DirectoryException(String message) {
        super(message);
    }

    public DirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}