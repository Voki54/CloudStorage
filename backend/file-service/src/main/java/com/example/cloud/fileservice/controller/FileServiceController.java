package com.example.cloud.fileservice.controller;


import com.example.cloud.fileservice.dto.UploadResponse;
import com.example.cloud.fileservice.service.FileManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                     @AuthenticationPrincipal Jwt jwt
    ) {
        UUID fileId = fileManagementService.uploadFile(file, jwt.getSubject());
        return ResponseEntity.ok(new UploadResponse(fileId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") UUID id,
                                           @AuthenticationPrincipal Jwt jwt
    ) {
        fileManagementService.deleteFile(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}