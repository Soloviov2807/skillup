package com.example.course_service.dto.section;

import com.example.course_service.dto.lesson.LessonResponse;

import java.util.List;

public record SectionResponse(
        long id,
        String name,
        List<LessonResponse> lessons
) {
}
