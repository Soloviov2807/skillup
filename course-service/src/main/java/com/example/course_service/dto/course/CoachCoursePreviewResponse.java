package com.example.course_service.dto.course;

import java.math.BigDecimal;

public record CoachCoursePreviewResponse(
        long courseId,
        String name,
        String previewUrl
) {
}
