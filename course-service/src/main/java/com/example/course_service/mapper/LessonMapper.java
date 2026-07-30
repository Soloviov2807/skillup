package com.example.course_service.mapper;


import com.example.course_service.model.Lesson;
import com.example.course_service.dto.lesson.LessonResponse;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonResponse toDto(Lesson lesson){
        return new LessonResponse(lesson.getId(),
                lesson.getName(),
                lesson.getDuration());
    }


}
