package com.example.course_service.service;


import com.example.course_service.controller.UserClient;
import com.example.course_service.dto.CoachInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;



    public CoachInfoResponse getCoachInfo(long coachId){

        ResponseEntity<CoachInfoResponse> response = userClient.getCoachInfo(coachId);


        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Failed to get coach info");
        }

        return response.getBody();

    }


}
