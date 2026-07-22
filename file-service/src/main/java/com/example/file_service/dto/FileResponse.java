package com.example.file_service.dto;

import java.time.Instant;

public record FileResponse(
        String name,
        String id,
        String url,
        String mimeType,
        Instant createdAt,
        long size
) {

}
