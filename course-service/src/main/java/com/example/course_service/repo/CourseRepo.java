package com.example.course_service.repo;

import com.example.course_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CourseRepo extends JpaRepository<Course, Long> {

    @Modifying
    @Transactional
    @Query("""
    UPDATE Course c
    SET c.coachName = :coachName,
        c.avatarImageId = :avatarId
    WHERE c.coachId = :coachId
    """)
    int updateCoachInfo(
            @Param("coachId") Long id,
            @Param("coachName") String name,
            @Param("avatarId") String avatarId);

    List<Course> findAllByCoachId(long coachId);

}
