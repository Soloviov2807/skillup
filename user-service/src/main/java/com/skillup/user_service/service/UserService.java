package com.skillup.user_service.service;

import com.example.common.event.CourseSaleCompletedEvent;
import com.skillup.user_service.controller.FileClient;
import com.skillup.user_service.exception.UserAlreadyExistsException;
import com.skillup.user_service.exception.UserNotFoundException;
import com.skillup.user_service.kafka.UserProducer;
import com.skillup.user_service.mapper.Mapper;
import com.skillup.user_service.model.Roles;
import com.skillup.user_service.model.User;
import com.skillup.user_service.model.UserPrincipal;
import com.skillup.user_service.model.dto.*;
import com.skillup.user_service.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepo repo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final FileClient fileClient;
    private final FileService fileService;
    private final Mapper mapper;
    private final UserProducer userProducer;

    public void register(RegisterRequest registerRequest) {

        boolean existUsernameOrEmail = repo.existsByNameOrEmail(registerRequest.name(), registerRequest.email());



        if(existUsernameOrEmail){
            throw new UserAlreadyExistsException("User with this username or email is already exists");
        }



        User user = User.builder()
                .name(registerRequest.name())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .roles(List.of(Roles.ROLE_USER))
                .build();


        repo.save(user);

        log.info("Successfully saved user: userName={}, email={}, roles={}", user.getName(), user.getEmail(), user.getRoles());

    }

    public String login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.name(),
                        loginRequest.password()));


            log.info("Generating jwt token for user: username={}", loginRequest.name());

            return jwtService.generateToken((UserPrincipal) authentication.getPrincipal());

    }





    public UserResponse getUserById(long userId) {

        log.info("Getting user by id from database: userId:{}", userId);
        User user = repo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        log.info("Found user from database: userId={}", userId);

        return mapper.toResponse(user);







    }



    public ProfileResponse getUserProfile(long userId) {

        User user = repo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        String avatarId = user.getAvatarId();
        String avatarUrl = null;

        if(avatarId != null){

            ResponseEntity<PublicUrlResponse> response = fileClient.getPublicUrl(avatarId);

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
                log.error("Failed to get avatar: userId={}, avatarId={}", userId, user.getAvatarId());
                throw new RuntimeException("Failed to get avatar from file service");
            }

            avatarUrl = response.getBody().url();
        }


        return new ProfileResponse(user.getName(), user.getEmail(), avatarUrl);

    }

    @Transactional
    public void updateUserProfile(long userId,
                                  UserRequest userRequest,
                                  MultipartFile file) throws IOException {

        User user = repo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String oldName = user.getName();
        String oldAvatarId = user.getAvatarId();

        user.setName(userRequest.name());
        user.setEmail(userRequest.email());

        String avatarIdForEvent = oldAvatarId;

        if (file != null && !file.isEmpty()) {

            String newAvatarId = fileService.uploadAvatarOrThrow(file);

            try {
                user.setAvatarId(newAvatarId);
                repo.save(user);

                avatarIdForEvent = newAvatarId;

            } catch (Exception e) {
                fileService.deleteFileSafe(newAvatarId);
                throw e;
            }

        } else {
            repo.save(user);
        }

        boolean profileChanged =
                !Objects.equals(oldName, userRequest.name()) ||
                        !Objects.equals(oldAvatarId, avatarIdForEvent);

        if (profileChanged) {
            userProducer.send(userId, userRequest.name(), avatarIdForEvent);

            if (!Objects.equals(oldAvatarId, avatarIdForEvent)) {
                fileService.deleteFileSafe(oldAvatarId);
            }
        }
    }


    public CoachInfoResponse getCoachInfo(long coachId) {

        User coach = repo.findById(coachId).orElseThrow(() -> new UserNotFoundException("User not found"));

        return new CoachInfoResponse(
                coachId,
                coach.getName(),
                coach.getAvatarId()
        );




    }

    @Transactional
    public void increaseBalance(CourseSaleCompletedEvent courseSaleCompletedEvent) {

        User coach = repo.findById(courseSaleCompletedEvent.coachId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        coach.setBalance(coach.getBalance().add(courseSaleCompletedEvent.amount()));

    }

    public BalanceResponse getUserBalance(long userId) {

        User user = repo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        return new BalanceResponse(user.getBalance());
    }
}
