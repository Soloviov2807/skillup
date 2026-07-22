package com.example.course_service.controller;

import com.example.course_service.model.JwtUserPrincipal;
import com.example.course_service.dto.review.ReviewRequest;
import com.example.course_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;


    @PostMapping("/{courseId}")
    public ResponseEntity<Void> addReview(@Valid @RequestBody ReviewRequest reviewRequest,
                                          @AuthenticationPrincipal JwtUserPrincipal principal,
                                          @PathVariable long courseId) {

        service.addReview(reviewRequest, principal.userId(), courseId, principal.username());

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal JwtUserPrincipal principal,
                                             @PathVariable long reviewId){

        service.deleteReview(principal.userId(), reviewId);

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@Valid @RequestBody ReviewRequest reviewRequest,
                                             @AuthenticationPrincipal JwtUserPrincipal principal,
                                             @PathVariable long reviewId){

        service.updateReview(reviewRequest, principal.userId(), reviewId, principal.username());

        return ResponseEntity.ok().build();

    }






}
