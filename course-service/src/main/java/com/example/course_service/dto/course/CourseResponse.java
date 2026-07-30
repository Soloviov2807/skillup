package com.example.course_service.dto.course;

import com.example.course_service.dto.review.ReviewResponse;
import com.example.course_service.dto.section.SectionResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(
    long courseId,
    String name,
    double rating,
    String description,
    BigDecimal price,
    List<SectionResponse> sections,
    List<ReviewResponse> reviews,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String previewUrl,
    String coachName,
    String avatarUrl
) {
}
