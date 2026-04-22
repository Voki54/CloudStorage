package com.example.cloud.fileservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class S3KeyGeneratorTest {

    @Test
    void generateKey_withExtension() {
        String key = S3KeyGenerator.generateKey("user1", "file.pdf");

        assertTrue(key.contains("files/"));
        assertTrue(key.contains("/user1/"));
        assertTrue(key.endsWith(".pdf"));
    }

    @Test
    void generateKey_withoutExtension() {
        String key = S3KeyGenerator.generateKey("user1", "file");

        assertFalse(key.matches(".*\\.[a-z0-9]+$"));
    }

    @Test
    void generateKey_nullFilename() {
        String key = S3KeyGenerator.generateKey("user1", null);

        assertNotNull(key);
        assertFalse(key.contains("."));
    }

    @Test
    void generateKey_invalidExtension() {
        String key = S3KeyGenerator.generateKey("user1", "file.verylongextension123");

        assertFalse(key.endsWith(".verylongextension123"));
    }

    @Test
    void generateKey_uppercaseExtension() {
        String key = S3KeyGenerator.generateKey("user1", "file.PDF");

        assertTrue(key.endsWith(".pdf"));
    }
}