package com.example.file_service.dto;

public record UploadFileResponse(
        String fileId,
        String publicUrl
) {
}
