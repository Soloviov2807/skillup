package com.skillup.user_service.repo;

import com.skillup.user_service.model.OAuthLoginCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthLoginCodeRepo
        extends JpaRepository<OAuthLoginCode, Long> {

    Optional<OAuthLoginCode> findByCode(String code);

    void deleteByCode(String code);
}
