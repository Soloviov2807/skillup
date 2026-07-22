package com.example.course_service.dto.course;

import java.math.BigDecimal;

public record CoursePreviewResponse(
        long courseId,
        String name,
        double rating,
        BigDecimal price,
        String previewUrl,
        String coachName
) {
}
