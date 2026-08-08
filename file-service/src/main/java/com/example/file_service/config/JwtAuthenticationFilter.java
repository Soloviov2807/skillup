package com.example.file_service.config;

import com.example.file_service.dto.JwtUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Value("${jwt.secret}")
    private  String key;

    private Key signingKey;

    @PostConstruct
    public void init(){
        byte[] keyInBytes = Base64.getDecoder().decode(key);
        signingKey = Keys.hmacShaKeyFor(keyInBytes);
    }





    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if(path.startsWith("/auth")){
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        try {
            String jwt = authHeader.substring(7);
            Claims claims = extractAllClaims(jwt);
            String userId = claims.getSubject();

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){

                String username = claims.get("username", String.class);
                List<?> roles = claims.get("roles", List.class);


                List<GrantedAuthority> authorities = roles == null
                        ? List.of()
                        : roles.stream()
                          .map(Object::toString)
                          .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                          .toList();

                if(isTokenValid(jwt)){

                    JwtUserPrincipal principal = new JwtUserPrincipal(Long.parseLong(claims.getSubject()), username);

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            principal,
                            jwt,
                            authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);


                }
            }
        } catch (JwtException e){
            SecurityContextHolder.clearContext();
        }


        filterChain.doFilter(request, response);
    }





    private boolean isTokenValid(String token){

        Date now = new Date();
        Claims claims = extractAllClaims(token);

        return !claims.getExpiration().before(now);
    }


    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }





}