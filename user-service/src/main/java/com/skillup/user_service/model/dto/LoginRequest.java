package com.skillup.user_service.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Username is required")
        String name,

        @NotBlank(message = "Password is required")
        String password
) {}
