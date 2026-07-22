package com.example.course_service.repo;

import com.example.course_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {


    boolean existsByUserIdAndCourseId(long userId, long courseId);


}
