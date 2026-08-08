package com.skillup.user_service.service;

import com.skillup.user_service.controller.FileClient;
import com.skillup.user_service.model.dto.UploadFileResponse;
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

    public void deleteFileSafe(String fileId){

        try {
            if(fileId != null){
                fileClient.deleteFileById(getAuthHeader(), fileId);
            }
        } catch (Exception e) {
        }

    }



    public String uploadAvatarOrThrow(MultipartFile file) throws IOException {

        ResponseEntity<UploadFileResponse> response = fileClient.uploadAvatar(file, getAuthHeader());


        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().fileId() == null){
            throw new RuntimeException("Failed to upload avatar");
        }

        return response.getBody().fileId();

    }


}
