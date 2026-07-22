package com.example.course_service.dto.course;

import java.math.BigDecimal;

public record MyCoursePreviewResponse(
        long courseId,
        String name,
        String previewUrl
) {
}
