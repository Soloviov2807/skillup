package com.example.file_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;





    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Публичные ссылки для картинок
                        .requestMatchers(
                                HttpMethod.GET,
                                "/files/*/public-url"
                        ).permitAll()

                        // Временные ссылки для видео/файлов
                        .requestMatchers(
                                HttpMethod.GET,
                                "/files/*/download-url"
                        ).authenticated()

                        // Все загрузки требуют JWT
                        .requestMatchers(
                                "/files/avatars",
                                "/files/course-covers",
                                "/files/videos",
                                "/files/attachments"
                        ).authenticated()

                        // Удаление требует JWT
                        .requestMatchers(
                                "/files/delete/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }








}

