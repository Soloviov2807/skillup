package com.example.course_service.controller;

import com.example.course_service.dto.CoachInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/auth/users/{coachId}/coach-info")
    public ResponseEntity<CoachInfoResponse> getCoachInfo(@PathVariable long coachId);

}
