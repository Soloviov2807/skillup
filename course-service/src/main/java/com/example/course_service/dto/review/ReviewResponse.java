package com.example.course_service.dto.review;

import java.time.LocalDateTime;

public record ReviewResponse(
        long id,
        String username,
        int rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
