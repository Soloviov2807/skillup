package com.example.course_service.controller;

import com.example.course_service.model.JwtUserPrincipal;
import com.example.course_service.dto.section.SectionRequest;
import com.example.course_service.dto.section.SectionResponse;
import com.example.course_service.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService service;

    @PostMapping("/{courseId}")
    public ResponseEntity<Void> addSection(@Valid @RequestBody SectionRequest sectionRequest,
                                           @AuthenticationPrincipal JwtUserPrincipal principal,
                                           @PathVariable long courseId){
        service.addSection(sectionRequest, principal.userId(), courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> getSection(@PathVariable long sectionId){

        return ResponseEntity.ok(service.getSectionById(sectionId));

    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<Void> updateSection(@Valid @RequestBody SectionRequest sectionRequest,
                                              @AuthenticationPrincipal JwtUserPrincipal principal,
                                              @PathVariable long sectionId){

        service.updateSection(sectionRequest, principal.userId(), sectionId);

        return ResponseEntity.ok().build();

    }


    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable long sectionId,
                                              @AuthenticationPrincipal JwtUserPrincipal principal){
        service.deleteSection(sectionId, principal.userId());
        return ResponseEntity.noContent().build();
    }



}
