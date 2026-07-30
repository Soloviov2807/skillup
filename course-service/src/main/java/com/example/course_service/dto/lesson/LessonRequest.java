package com.example.course_service.dto.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LessonRequest(

        @Size(min = 5, max = 30, message = "Lesson name must be 5-30 characters")
        @NotBlank(message = "Lesson name is required")
        String name
) {
}
