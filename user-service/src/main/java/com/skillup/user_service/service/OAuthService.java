package com.skillup.user_service.service;

import com.skillup.user_service.model.*;
import com.skillup.user_service.repo.OAuthAccountRepo;
import com.skillup.user_service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepo userRepo;
    private final OAuthAccountRepo oauthAccountRepo;
    private final JwtService jwtService;


    public String loginWithGoogle(OAuth2User oauthUser){

        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        var oAuthAccount = oauthAccountRepo.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, googleId);

        if(oAuthAccount.isPresent()){
            User user = oAuthAccount.get().getUser();

            return jwtService.generateToken(new UserPrincipal(user));
        }

        User user = userRepo.findByEmail(email)
                .orElseGet(() -> createUser(name, email));

        OAuthAccount newAccount = OAuthAccount.builder()
                .user(user)
                .provider(OAuthProvider.GOOGLE)
                .providerUserId(googleId)
                .build();

        oauthAccountRepo.save(newAccount);

        return jwtService.generateToken(
                new UserPrincipal(user)
        );


    }

    private User createUser(String name, String email) {

        User user = User.builder()
                .name(name)
                .email(email)
                .password(null)
                .roles(List.of(Roles.ROLE_USER
                ))
                .build();

        return userRepo.save(user);
    }
}