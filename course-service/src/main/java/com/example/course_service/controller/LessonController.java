package com.example.course_service.controller;


import com.example.course_service.model.JwtUserPrincipal;
import com.example.course_service.dto.DownloadUrl;
import com.example.course_service.dto.lesson.LessonRequest;
import com.example.course_service.dto.lesson.LessonResponse;
import com.example.course_service.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService service;


    @PostMapping("/{sectionId}")
    public ResponseEntity<Void> addLesson(@Valid @RequestPart LessonRequest lessonRequest,
                                          @RequestPart MultipartFile video,
                                          @AuthenticationPrincipal JwtUserPrincipal principal,
                                          @PathVariable long sectionId) throws IOException {

        service.addLesson(lessonRequest, principal.userId(), video, sectionId);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> getLesson(@PathVariable long lessonId){

        return ResponseEntity.ok(service.getLesson(lessonId));
    }


    @GetMapping("/{lessonId}/video")
    public ResponseEntity<DownloadUrl> getLessonVideo(@PathVariable long lessonId){

        ResponseEntity<DownloadUrl> response = service.getLessonVideo(lessonId);
        return ResponseEntity.ok(response.getBody());
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable long lessonId,
                                                  @AuthenticationPrincipal JwtUserPrincipal principal){
        service.deleteLesson(lessonId, principal.userId());

        return ResponseEntity.noContent().build();

    }





}
