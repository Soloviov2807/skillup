package com.example.course_service.service;

import com.example.course_service.exception.CourseNotFound;
import com.example.course_service.model.Course;
import com.example.course_service.model.Review;
import com.example.course_service.dto.review.ReviewRequest;
import com.example.course_service.repo.CourseRepo;
import com.example.course_service.repo.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final CourseRepo courseRepo;

    @Transactional
    public void addReview(ReviewRequest reviewRequest, long userId, long courseId, String username) {

        Course course = courseRepo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));


        if(course.getCoachId() == userId){
            throw new RuntimeException("You cant rate your own course");
        }

        if(reviewRepo.existsByUserIdAndCourseId(userId, courseId)){
            throw new RuntimeException("There is already review");
        }



        Review review = new Review();
        
        review.setComment(reviewRequest.comment());
        review.setRating(reviewRequest.rating());
        review.setUsername(username);
        review.setCourse(course);
        review.setUserId(userId);



        course.setTotalRating(course.getTotalRating() + reviewRequest.rating());
        course.setReviewsCount(course.getReviewsCount() + 1);

        double avg = (double) course.getTotalRating() / course.getReviewsCount();

        course.setRating(Math.round(avg * 10.0) / 10.0);

        reviewRepo.save(review);


    }

    @Transactional
    public void deleteReview(long userId, long reviewId) {

        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if(review.getUserId() != userId){
            throw new AccessDeniedException("Not your review");
        }


        Course course = review.getCourse();

        course.setTotalRating(course.getTotalRating() - review.getRating());
        course.setReviewsCount(course.getReviewsCount() - 1);

         if(course.getReviewsCount() == 0){
             course.setRating(0);
         } else {
             double avg = (double) course.getTotalRating() / course.getReviewsCount();

             course.setRating(Math.round(avg * 10.0) / 10.0);
         }

        reviewRepo.deleteById(reviewId);

    }


    @Transactional
    public void updateReview(ReviewRequest reviewRequest, long userId, long reviewId, String username) {

        Review review = reviewRepo.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));

        if(review.getUserId() != userId){
            throw new AccessDeniedException("Not your review");
        }
        int oldRating = review.getRating();
        int newRating = reviewRequest.rating();

        review.setRating(newRating);
        review.setComment(reviewRequest.comment());
        review.setUsername(username);

        Course course = review.getCourse();


        if(oldRating != newRating){
            course.setTotalRating(course.getTotalRating() - oldRating + newRating);

            double avg = (double) course.getTotalRating() / course.getReviewsCount();

            course.setRating(Math.round(avg * 10.0) / 10.0);
        }

    }
}
