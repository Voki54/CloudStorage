package com.example.cloud.fileservice;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileServiceController {

    private final FileStorageService fileStorageService;

//    @GetMapping("/hello")
//    public String hello(Authentication authentication) {
//        return "Hello, " + authentication.getName();
//    }

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal Jwt jwt) {
        return "Hello, " + jwt.getSubject();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @AuthenticationPrincipal Jwt jwt
    ) {
        String key = fileStorageService.uploadFile(file, jwt.getSubject());
        return ResponseEntity.ok("File uploaded successfully: " + key);
    }
}