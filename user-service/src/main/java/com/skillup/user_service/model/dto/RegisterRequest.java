package com.skillup.user_service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record RegisterRequest(

        @Size(min = 4, max = 30, message = "Username must be 4-30 characters")
        @NotBlank(message = "Username is required")
        String name,

        @Size(min = 8, message = "Password must be at least 8 characters")
        @NotBlank(message = "Password is required")
        String password,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email
) {
}
