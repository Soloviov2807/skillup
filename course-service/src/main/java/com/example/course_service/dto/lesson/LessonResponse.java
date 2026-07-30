package com.example.course_service.dto.lesson;

public record LessonResponse(
        long id,
        String name,
        int duration
) {
}
