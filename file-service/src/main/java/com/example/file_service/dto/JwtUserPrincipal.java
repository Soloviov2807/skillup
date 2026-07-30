package com.example.file_service.dto;

public record JwtUserPrincipal(
        Long userId,
        String username
) {
}
