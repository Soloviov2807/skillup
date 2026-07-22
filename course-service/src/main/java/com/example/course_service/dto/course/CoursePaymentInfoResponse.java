package com.example.course_service.dto.course;

import java.math.BigDecimal;

public record CoursePaymentInfoResponse(
        Long courseId,
        String courseName,
        BigDecimal price
) {
}