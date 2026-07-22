package com.skillup.user_service.controller;

import com.skillup.user_service.config.FeignConfig;
import com.skillup.user_service.model.dto.DownloadUrl;
import com.skillup.user_service.model.dto.PublicUrlResponse;
import com.skillup.user_service.model.dto.UploadFileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(value = "FILE-SERVICE", configuration = FeignConfig.class)
public interface FileClient {

    @PostMapping(value = "/files/avatars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UploadFileResponse> uploadAvatar(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String token
    );


    @GetMapping("files/{fileId}/public-url")
    public ResponseEntity<PublicUrlResponse> getPublicUrl(@PathVariable String fileId);

    @DeleteMapping("files/delete/{fileId}")
    public ResponseEntity<Void> deleteFileById(@RequestHeader("Authorization") String token, @PathVariable String fileId);

}
