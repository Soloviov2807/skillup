package com.example.course_service.mapper;

import com.example.course_service.model.Section;
import com.example.course_service.dto.section.SectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionMapper {

    private final LessonMapper lessonMapper;

    public SectionResponse toDto(Section section){
        return new SectionResponse(section.getId(),
                section.getName(),
                section.getLessons().stream()
                        .map(lessonMapper::toDto)
                        .toList());
    }




}
