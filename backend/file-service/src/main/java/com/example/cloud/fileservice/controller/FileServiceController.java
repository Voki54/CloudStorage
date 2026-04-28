package com.example.cloud.fileservice.controller;


import com.example.cloud.fileservice.dto.FileDetailsDto;
import com.example.cloud.fileservice.dto.FileDownloadData;
import com.example.cloud.fileservice.dto.FileMetadataDto;
import com.example.cloud.fileservice.dto.UploadResponse;
import com.example.cloud.fileservice.service.FileManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileServiceController {

    private final FileManagementService fileManagementService;

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal Jwt jwt) {
        return "Hello, " + jwt.getSubject();
    }

    @GetMapping
    public ResponseEntity<List<FileMetadataDto>> getFilesMetadata(@AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(fileManagementService.getMetadataForAllUserFiles(jwt.getSubject()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                     @AuthenticationPrincipal Jwt jwt
    ) {
        UUID fileId = fileManagementService.uploadFile(file, jwt.getSubject());
        return ResponseEntity.ok(new UploadResponse(fileId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDetailsDto> getFileDetails(@PathVariable("id") UUID id,
                                                         @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(fileManagementService.getFileDetails(id, jwt.getSubject()));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        FileDownloadData file = fileManagementService.downloadFile(id, jwt.getSubject());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.originalName(), StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.size())
                .body(new InputStreamResource(file.data()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") UUID id,
                                           @AuthenticationPrincipal Jwt jwt
    ) {
        fileManagementService.deleteFile(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}