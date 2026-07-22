package com.example.course_service.service;

import com.example.course_service.exception.CourseNotFound;
import com.example.course_service.kafka.CourseSaleProducer;
import com.example.course_service.model.Course;
import com.example.course_service.repo.CourseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseRepo courseRepo;
    private final CourseSaleProducer saleProducer;

    @Transactional
    public void enroll(Long courseId, Long userId, BigDecimal price){

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new CourseNotFound("Course not found"));

        if (course.getStudentsIds().contains(userId)) {
            return;
        }


        course.getStudentsIds().add(userId);

        courseRepo.save(course);

        saleProducer.send(userId, price);
    }
}