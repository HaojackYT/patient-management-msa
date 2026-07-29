package com.pm.auth_service.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(
                secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email) // Standard field mainly used to store an ID that relates to the person,
                                // who is trying to login
                .claim("role", role) // Custom field
                .issuedAt(new Date()) // is used to determine if the token is valid or not
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10-hour (development purpose)
                .signWith(secretKey) // Encode
                .compact();
    }

}
