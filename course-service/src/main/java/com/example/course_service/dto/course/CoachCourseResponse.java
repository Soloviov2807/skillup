package com.example.course_service.dto.course;

import com.example.course_service.dto.review.ReviewResponse;
import com.example.course_service.dto.section.SectionResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CoachCourseResponse(
        long courseId,
        String name,
        String description,
        BigDecimal price,
        List<SectionResponse> sections,
        String previewUrl
) {
}
