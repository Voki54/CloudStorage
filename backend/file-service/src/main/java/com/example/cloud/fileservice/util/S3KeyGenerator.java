package com.example.cloud.fileservice.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for generating S3 object keys for a file service.
 */
public class S3KeyGenerator {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.of("UTC"));

    /**
     * Generates S3 key.
     *
     * @param userId The identifier of the user
     * @param originalFilename The original file name (optional)
     * @return A unique S3 object key following the pattern:
     *         files/yyyy/MM/dd/userId/uuid.ext
     */
    public static String generateKey(String userId, String originalFilename) {
        String datePath = DATE_FORMAT.format(Instant.now());
        String ext = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();

        return String.format("files/%s/%s/%s%s", datePath, userId, uuid, ext);
    }

    /**
     * Extracts the file extension from the original filename.
     * Returns empty string if no extension exists, or it's too long.
     *
     * @param filename Original filename
     * @return File extension including dot, e.g., ".pdf", or empty string
     */
    private static String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) return "";
        String cleanName = filename.toLowerCase().trim();
        int idx = cleanName.lastIndexOf('.');
        if (idx == -1 || idx == cleanName.length() - 1) return "";
        String ext = cleanName.substring(idx + 1);
        if (ext.matches("[a-z0-9]{1,10}")) {
            return "." + ext;
        }
        return "";
    }
}
