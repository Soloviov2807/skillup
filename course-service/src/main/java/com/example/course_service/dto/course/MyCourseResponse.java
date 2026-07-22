package com.example.course_service.dto.course;

import com.example.course_service.dto.review.ReviewResponse;
import com.example.course_service.dto.section.SectionResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MyCourseResponse(
        long courseId,
        String name,
        List<SectionResponse> sections
) {
}
