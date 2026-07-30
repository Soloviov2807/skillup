package com.skillup.user_service.model.dto;

public record UploadFileResponse(
        String fileId,
        String publicUrl
) {
}
