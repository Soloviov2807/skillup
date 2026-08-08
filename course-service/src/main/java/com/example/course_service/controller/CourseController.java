package com.example.course_service.controller;


import com.example.course_service.dto.course.*;
import com.example.course_service.dto.section.SectionResponse;
import com.example.course_service.model.JwtUserPrincipal;
import com.example.course_service.dto.DownloadUrl;
import com.example.course_service.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;
    private final ObjectMapper objectMapper;


    @GetMapping("/{courseId}/purchased")
    public ResponseEntity<Boolean> isPurchased(@PathVariable long courseId, @AuthenticationPrincipal JwtUserPrincipal principal){
        return ResponseEntity.ok(service.isPurchased(principal.userId(), courseId));
    }

    @GetMapping("/{courseId}/coach")
    public ResponseEntity<CoachCourseResponse> getCoachCourse(@PathVariable long courseId, @AuthenticationPrincipal JwtUserPrincipal principal){
        return ResponseEntity.ok(service.getCoachCourse(principal.userId(), courseId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<MyCoursePreviewResponse>> getMyCourses(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {

        return ResponseEntity.ok(
                service.getMyCourses(principal.userId())
        );

    }

    @GetMapping("/coach")
    public ResponseEntity<List<CoachCoursePreviewResponse>> getCoachCourses(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {

        return ResponseEntity.ok(
                service.getCoachCourses(principal.userId())
        );

    }



    @GetMapping("/my/{courseId}")
    public ResponseEntity<MyCourseResponse> getMyCourse(@PathVariable long courseId, @AuthenticationPrincipal JwtUserPrincipal principal){

        return ResponseEntity.ok(service.getMyCourse(principal.userId(), courseId));

    }



    @GetMapping
    public ResponseEntity<List<CoursePreviewResponse>> getAllCourses(){
        List<CoursePreviewResponse> courses = service.getAllCourses();

        return ResponseEntity.ok(courses);
    }


    @PostMapping
    public ResponseEntity<Void> addCourse(
            @RequestPart("courseRequest") String courseJson,
            @RequestPart(required = false) MultipartFile preview,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) throws IOException {

        CourseRequest courseRequest = objectMapper
                .readValue(
                        courseJson,
                        CourseRequest.class);

        service.addCourse(courseRequest.name(), courseRequest.description(), courseRequest.price(), principal.userId(), preview);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable long courseId){

        return ResponseEntity.ok(service.getCourseById(courseId));


    }


    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourseById(@PathVariable long courseId, @AuthenticationPrincipal JwtUserPrincipal principal){

        service.deleteCourseById(courseId, principal.userId());

        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{courseId}")
    public ResponseEntity<Void> updateCourse(
            @RequestPart("courseRequest") String courseJson,
            @RequestPart(required = false) MultipartFile preview,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable long courseId
    ) throws IOException {

        CourseRequest courseRequest = objectMapper
                .readValue(
                        courseJson,
                        CourseRequest.class);

        service.updateCourse(courseRequest.name(), courseRequest.description(), courseRequest.price(), principal.userId(), preview, courseId);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/{courseId}/payment-info")
    public ResponseEntity<CoursePaymentInfoResponse> getCoursePaymentInfo(@PathVariable long courseId){

        return ResponseEntity.ok(service.getCoursePaymentInfoById(courseId));
    }

















}
