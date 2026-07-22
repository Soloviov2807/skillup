package com.example.course_service.model;

public record JwtUserPrincipal(
    Long userId,
    String username
) {
}
