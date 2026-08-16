package com.skillup.user_service.service;

import com.skillup.user_service.model.OAuthLoginCode;
import com.skillup.user_service.repo.OAuthLoginCodeRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthLoginCodeService {

    private final OAuthLoginCodeRepo repo;

    public String createCode(String jwt) {

        String code = UUID.randomUUID().toString();

        OAuthLoginCode loginCode = OAuthLoginCode.builder()
                .code(code)
                .jwt(jwt)
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        repo.save(loginCode);

        return code;
    }


    @Transactional
    public String exchangeCode(String code) {

        OAuthLoginCode loginCode = repo.findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException("Invalid OAuth code"));

        if (loginCode.getExpiresAt().isBefore(Instant.now())) {

            repo.delete(loginCode);

            throw new RuntimeException("OAuth code expired");
        }

        String jwt = loginCode.getJwt();


        repo.delete(loginCode);

        return jwt;
    }
}
