package com.example.file_service.controller;

import com.example.file_service.dto.*;
import com.example.file_service.mapper.FileMapper;
import com.example.file_service.repository.FileMetaData;
import com.example.file_service.service.FileCategory;
import com.example.file_service.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;


    @PostMapping("/avatars")
    public ResponseEntity<UploadFileResponse> uploadAvatar(@RequestPart("file")MultipartFile file, @AuthenticationPrincipal JwtUserPrincipal principal) throws IOException {


           UploadFileResponse uploadFileResponse = fileService.uploadFile(file, principal.userId(), FileCategory.AVATAR);

            return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }

    @PostMapping("/course-covers")
    public ResponseEntity<UploadFileResponse> uploadCourseCover(@RequestPart("file")MultipartFile file, @AuthenticationPrincipal JwtUserPrincipal principal) throws IOException {


        UploadFileResponse uploadFileResponse = fileService.uploadFile(file, principal.userId(), FileCategory.COURSE_COVER);

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }

    @PostMapping(
            value = "/videos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UploadFileResponse> uploadVideo(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) throws IOException {

        UploadFileResponse response =
                fileService.uploadFile(file, principal.userId(), FileCategory.VIDEO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/attachments")
    public ResponseEntity<UploadFileResponse> uploadAttachment(@RequestPart("file")MultipartFile file, @AuthenticationPrincipal JwtUserPrincipal principal) throws IOException {


        UploadFileResponse uploadFileResponse = fileService.uploadFile(file, principal.userId(), FileCategory.ATTACHMENT);

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFileResponse);
    }


    @GetMapping("/{fileId}/download-url")
    public ResponseEntity<DownloadUrl> getFile(@PathVariable String fileId, @AuthenticationPrincipal JwtUserPrincipal principal) {


        return ResponseEntity.ok(fileService.generateDownloadUrl(fileId));
    }


    @GetMapping("/{fileId}/public-url")
    public ResponseEntity<PublicUrlResponse> getPublicUrl(@PathVariable String fileId){

        return ResponseEntity.ok(fileService.getPublicUrl(fileId));

    }


    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<Void> deleteFileById(@AuthenticationPrincipal JwtUserPrincipal principal, @PathVariable String fileId) throws IOException {


        fileService.deleteFileById(principal.userId(), fileId);

        return ResponseEntity.noContent().build();

    }




}
