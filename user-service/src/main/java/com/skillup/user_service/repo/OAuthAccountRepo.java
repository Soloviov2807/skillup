package com.skillup.user_service.repo;

import com.skillup.user_service.model.OAuthAccount;
import com.skillup.user_service.model.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepo
        extends JpaRepository<OAuthAccount, Long> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(
            OAuthProvider provider,
            String providerUserId
    );
}