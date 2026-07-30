package com.example.course_service.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CourseRequest(


        @Size(min = 10, max = 30, message = "Course name must be 10-30 characters")
        @NotBlank(message = "Course name is required")
        String name,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @Size(min = 80, max = 1000, message = "Course description must be 80-1000 characters")
        String description




) {
}
