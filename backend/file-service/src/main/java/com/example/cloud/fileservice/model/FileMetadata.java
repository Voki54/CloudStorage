package com.example.cloud.fileservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "file_metadata",
        indexes = {
                @Index(name = "idx_owner_id", columnList = "ownerId"),
                @Index(name = "idx_storage_key", columnList = "storageKey")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String storageKey;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false, length = 64)
    private String hash;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;
}