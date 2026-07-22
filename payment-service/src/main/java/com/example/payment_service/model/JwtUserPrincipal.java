package com.example.payment_service.model;

public record JwtUserPrincipal(
    Long userId,
    String username
) {
}
