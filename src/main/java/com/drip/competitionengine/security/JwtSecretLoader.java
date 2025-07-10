package com.drip.competitionengine.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
class JwtSecretLoader {

    @Value("${jwt.secret-file}")
    private Path secretPath;

    private SecretKey secretKey;

    @PostConstruct
    void init() throws Exception {
        byte[] raw = Files.readAllBytes(secretPath);
        secretKey = Keys.hmacShaKeyFor(raw);
    }

    SecretKey getSecretKey() {
        return secretKey;
    }
}
