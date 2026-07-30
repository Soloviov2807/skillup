package com.example.course_service.mapper;

import com.example.course_service.dto.course.CoachCourseResponse;
import com.example.course_service.dto.course.MyCourseResponse;
import com.example.course_service.model.Course;
import com.example.course_service.dto.course.CoursePreviewResponse;
import com.example.course_service.dto.course.CourseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final SectionMapper sectionMapper;
    private final ReviewMapper reviewMapper;


    public CourseResponse toDto(Course course, String previewUrl, String avatarUrl){

        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getRating(),
                course.getDescription(),
                course.getPrice(),
                course.getSections().stream()
                        .map(sectionMapper::toDto)
                        .toList(),
                course.getReviews().stream().map(reviewMapper::toDto).toList(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                previewUrl,
                course.getCoachName(),
                avatarUrl);
    }

    public MyCourseResponse toMyCourseDto(Course course){

        return new MyCourseResponse(
                course.getId(),
                course.getName(),
                course.getSections().stream()
                        .map(sectionMapper::toDto)
                        .toList()
        );

    }


    public CoachCourseResponse toCoachCourseDto(Course course, String previewUrl){

        return new CoachCourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getPrice(),
                course.getSections().stream()
                        .map(sectionMapper::toDto)
                        .toList(),
                previewUrl
        );

    }

}
