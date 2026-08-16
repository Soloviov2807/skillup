package com.skillup.user_service.controller;

import com.skillup.user_service.service.OAuthLoginCodeService;
import com.skillup.user_service.service.OAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final OAuthService oauthService;
    private final OAuthLoginCodeService oauthLoginCodeService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        String jwt =
                oauthService.loginWithGoogle(oauthUser);

        String code =
                oauthLoginCodeService.createCode(jwt);

        response.sendRedirect(
                "http://16.170.166.106/oauth-success?code=" + code
        );
    }
}