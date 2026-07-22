package com.example.course_service.dto;

public record UploadFileResponse(
        String fileId,
        String publicUrl
) {
}
