package com.example.course_service.service;

import com.example.course_service.controller.FileClient;
import com.example.course_service.dto.DownloadUrl;
import com.example.course_service.dto.PublicUrlResponse;
import com.example.course_service.dto.UploadFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileClient fileClient;


    private String getAuthHeader(){
        String token = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getCredentials();

        return  "Bearer " + token;
    }

    public String uploadCourseCoverOrThrow(MultipartFile file) throws IOException {

        ResponseEntity<UploadFileResponse> response = fileClient.uploadCourseCover(file, getAuthHeader());

        String fileId = response.getBody().fileId();

        if(!response.getStatusCode().is2xxSuccessful() || fileId == null){
            throw new RuntimeException("Failed to upload preview");
        }

        return fileId;

    }

    public String uploadAttachmentOrThrow(MultipartFile file) throws IOException {

        ResponseEntity<UploadFileResponse> response = fileClient.uploadAttachment(file, getAuthHeader());

        String fileId = response.getBody().fileId();

        if(!response.getStatusCode().is2xxSuccessful() || fileId == null){
            throw new RuntimeException("Failed to upload preview");
        }

        return fileId;

    }

    public String uploadVideoOrThrow(MultipartFile file) throws IOException {

        ResponseEntity<UploadFileResponse> response = fileClient.uploadVideo(file, getAuthHeader());

        String fileId = response.getBody().fileId();

        if(!response.getStatusCode().is2xxSuccessful() || fileId == null){
            throw new RuntimeException("Failed to upload preview");
        }

        return fileId;

    }



    public void deleteFileSafe(String fileId){

        try {
            if(fileId != null){
                fileClient.deleteFileById(getAuthHeader(), fileId);
            }
        } catch (Exception _) {
        }

    }


    public ResponseEntity<DownloadUrl> getFile(String fileId){

        ResponseEntity<DownloadUrl> response = fileClient.getFile(fileId, getAuthHeader());


        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new RuntimeException("Failed to download video");
        }

        return response;

    }

    public PublicUrlResponse getPublicFile(String fileId){

        ResponseEntity<PublicUrlResponse> response = fileClient.getPublicUrl(fileId);

        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new RuntimeException("Failed to get public url");
        }

        return response.getBody();


    }


}
