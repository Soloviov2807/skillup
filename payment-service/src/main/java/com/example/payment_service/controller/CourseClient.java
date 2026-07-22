package com.example.payment_service.controller;

import com.example.payment_service.dto.CoursePaymentInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "COURSE-SERVICE")
public interface CourseClient {



    @GetMapping("/courses/{courseId}/payment-info")
    public ResponseEntity<CoursePaymentInfoResponse> getCoursePaymentInfo(@PathVariable long courseId);

    @GetMapping("/courses/{courseId}/purchased")
    public ResponseEntity<Boolean> isPurchased(@PathVariable long courseId, @RequestHeader("Authorization") String token);




}
