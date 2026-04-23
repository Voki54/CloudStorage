package com.example.cloud.fileservice.dto;

import java.io.InputStream;

public record FileDownloadData(
        InputStream data,
        String originalName,
        String contentType,
        long size
) {}
