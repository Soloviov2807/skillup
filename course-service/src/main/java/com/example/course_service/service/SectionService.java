package com.example.course_service.service;

import com.example.course_service.mapper.SectionMapper;
import com.example.course_service.model.Course;
import com.example.course_service.model.Section;
import com.example.course_service.dto.section.SectionRequest;
import com.example.course_service.dto.section.SectionResponse;
import com.example.course_service.repo.CourseRepo;
import com.example.course_service.repo.SectionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final CourseRepo courseRepo;
    private final SectionRepo sectionRepo;
    private final SectionMapper sectionMapper;


    @Transactional
    public void addSection(SectionRequest sectionRequest, long userId, long courseId) {

        Course course = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        if(course.getCoachId() != userId){
            throw new RuntimeException("Not your course");
        }

        Section section = new Section();

        section.setDuration(0);
        section.setName(sectionRequest.name());
        section.setCourse(course);

        sectionRepo.save(section);

    }

    public SectionResponse getSectionById(long sectionId) {

        Section section = sectionRepo.findById(sectionId).orElseThrow(() -> new RuntimeException("Section not found"));

        return sectionMapper.toDto(section);


    }

    @Transactional
    public void updateSection(SectionRequest sectionRequest, long userId, long sectionId) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));


        if(section.getCourse().getCoachId() != userId){
            throw new AccessDeniedException("Not your course");
        }

        if(sectionRequest.name() != null){
            section.setName(sectionRequest.name());
        }

        sectionRepo.save(section);

    }


    @Transactional
    public void deleteSection(long sectionId, long userId) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));


        if(section.getCourse().getCoachId() != userId){
            throw new AccessDeniedException("Not your course");
        }

        sectionRepo.deleteById(sectionId);


    }
}
