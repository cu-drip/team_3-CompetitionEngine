package com.drip.competitionengine.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtSecretLoader secretLoader;
    private SecretKey secret;              // кэшируем после @PostConstruct

    @PostConstruct
    void init() {
        this.secret = secretLoader.getSecretKey();
    }

    public Claims parse(String bearer) {
        String token = bearer != null && bearer.startsWith("Bearer ")
                ? bearer.substring(7)
                : bearer;
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String bearer) {
        try { parse(bearer); return true; }
        catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }
}
