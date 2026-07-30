package com.skillup.user_service.model.dto;

public record ProfileResponse(
        String name,
        String email,
        String avatarUrl

) {
}
