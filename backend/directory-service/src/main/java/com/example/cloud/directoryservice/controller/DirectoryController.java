package com.example.cloud.directoryservice.controller;

import com.example.cloud.directoryservice.dto.CreateDirectoryRequest;
import com.example.cloud.directoryservice.dto.CreateDirectoryResponse;
import com.example.cloud.directoryservice.dto.DirectoryDto;
import com.example.cloud.directoryservice.mapper.DirectoryMapper;
import com.example.cloud.directoryservice.model.DirectoryEntity;
import com.example.cloud.directoryservice.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/directories")
@RequiredArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal Jwt jwt) {
        return "Hello, " + jwt.getSubject();
    }

    @PostMapping
    public ResponseEntity<CreateDirectoryResponse> createDirectory(@RequestBody CreateDirectoryRequest request,
                                                                   @AuthenticationPrincipal Jwt jwt
    ) {
        DirectoryEntity directory = directoryService.create(request, jwt.getSubject());

        return ResponseEntity.ok(DirectoryMapper.toCreateResponse(directory));
    }

    @GetMapping
    public DirectoryDto getRoot(@AuthenticationPrincipal Jwt jwt) {
        return DirectoryMapper.toDto(directoryService.getOrCreateRoot(jwt.getSubject()));
    }

    @GetMapping("/{id}")
    public List<DirectoryDto> getDirectoryChildren(@PathVariable("id") UUID id,
                                                   @AuthenticationPrincipal Jwt jwt) {
        return directoryService.getChildren(id, jwt.getSubject())
                .stream()
                .map(DirectoryMapper::toDto)
                .toList();
    }
}
