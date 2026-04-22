package com.example.cloud.fileservice.util;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.MessageDigest;

@Slf4j
public class HashUtil {

    public static String sha256(InputStream inputStream) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] buffer = new byte[8192];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }

        byte[] hashBytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
