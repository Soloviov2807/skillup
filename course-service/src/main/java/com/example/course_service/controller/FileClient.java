package com.example.course_service.controller;

import com.example.course_service.config.FeignConfig;
import com.example.course_service.dto.DownloadUrl;
import com.example.course_service.dto.PublicUrlResponse;
import com.example.course_service.dto.UploadFileResponse;
import com.example.course_service.model.JwtUserPrincipal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@FeignClient(value = "FILE-SERVICE", configuration = FeignConfig.class)
public interface FileClient {



    @PostMapping(
            value = "/files/course-covers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<UploadFileResponse> uploadCourseCover(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String token
    );


    @PostMapping(
            value = "/files/videos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<UploadFileResponse> uploadVideo(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String token
    ) throws IOException;


    @PostMapping("/files/attachments")
    public ResponseEntity<UploadFileResponse> uploadAttachment(@RequestPart("file")MultipartFile file, @RequestHeader("Authorization") String token);


    @GetMapping("/files/{fileId}/download-url")
    ResponseEntity<DownloadUrl> getFile(
            @PathVariable String fileId,
            @RequestHeader("Authorization") String token
    );

    @DeleteMapping("/files/delete/{fileId}")
    public ResponseEntity<Void> deleteFileById(@RequestHeader("Authorization") String token, @PathVariable String fileId);


    @GetMapping("/files/{fileId}/public-url")
    public ResponseEntity<PublicUrlResponse> getPublicUrl(@PathVariable String fileId);

}
