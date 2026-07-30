package com.example.course_service.mapper;

import com.example.course_service.model.Review;
import com.example.course_service.dto.review.ReviewResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {

    public ReviewResponse toDto(Review review){

        return new ReviewResponse(
                review.getReviewId(),
                review.getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt());
    }

    public List<ReviewResponse> toDtos(List<Review> reviews){
        return reviews
                .stream()
                .map(this::toDto)
                .toList();
    }
}
