package com.example.payment_service.dto;


import java.math.BigDecimal;

public record CoursePaymentInfoResponse(
        Long courseId,
        String courseName,
        BigDecimal price
) {
}