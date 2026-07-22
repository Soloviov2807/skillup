package com.skillup.user_service.service;
import com.skillup.user_service.model.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final Key key;

    public JwtService(@Value("${jwt.secret}") String secretKeyInString){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyInString));
    }

    public String generateToken(UserPrincipal userPrincipal) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        claims.put("username", userPrincipal.getUsername());


        String userId = String.valueOf(userPrincipal.getUserId());
        return createToken(claims, userId);


    }

    private String createToken(Map<String, Object> claims, String subject) {

        Date now = new Date();
        Date expDate = new Date(now.getTime() + 1000 * 60 * 60 * 3);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setExpiration(expDate)
                .setIssuedAt(now)
                .signWith(key)
                .compact();
    }




}
