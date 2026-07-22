package com.skillup.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.user_service.model.JwtUserPrincipal;
import com.skillup.user_service.model.dto.*;
import com.skillup.user_service.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final ObjectMapper objectMapper;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest registerRequest){


        service.register(registerRequest);
        return ResponseEntity.status(201).build();


    }



    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        String jwtToken = service.login(loginRequest);

        return ResponseEntity.ok(new LoginResponse(jwtToken));
    }




    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId){
        UserResponse userResponse = service.getUserById(userId);

        return ResponseEntity.ok(userResponse);
    }


    @GetMapping("users/{coachId}/coach-info")
    public ResponseEntity<CoachInfoResponse> getCoachInfo(@PathVariable long coachId){

        return ResponseEntity.ok(service.getCoachInfo(coachId));
    }




    @GetMapping("users/me")
    public ResponseEntity<ProfileResponse> getUserProfile(@AuthenticationPrincipal JwtUserPrincipal principal){

        return ResponseEntity.ok(service.getUserProfile(principal.userId()));

    }


    @GetMapping("users/me/balance")
    public ResponseEntity<BalanceResponse> getUserBalance(@AuthenticationPrincipal JwtUserPrincipal principal){

        return ResponseEntity.ok(service.getUserBalance(principal.userId()));

    }

    @PutMapping("users/me/edit")
    public ResponseEntity<Void> updateUserProfile(@AuthenticationPrincipal JwtUserPrincipal principal, @Valid @RequestPart("userRequest") String userRequestJson, @RequestPart(required = false) MultipartFile avatar) throws IOException {


        UserRequest userRequest = objectMapper.readValue(userRequestJson, UserRequest.class);

        service.updateUserProfile(principal.userId(), userRequest, avatar);
        return ResponseEntity.ok().build();

    }








}
