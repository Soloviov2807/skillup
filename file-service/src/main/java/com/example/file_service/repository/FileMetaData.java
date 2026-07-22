package com.example.file_service.repository;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("file_metadata")
public record FileMetaData(
        String storageKey,
        String originalName,
        String mimeType,
        long userId,
        long size,
        Instant createdAt,
        @Id
        ObjectId fileId
) {
}
