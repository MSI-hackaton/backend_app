package dev.msi_hackaton.backend_app.service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.msi_hackaton.backend_app.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private static final long JWT_EXPIRATION_SECONDS = 60 * 60 * 24;

    /**
     * Генерация токена
     *
     * @param user данные пользователя
     * @return токен
     */
    public String generateToken(UserDto user) {

        Instant now = Instant.now();

        return Jwts.builder()
                .claim("uid", user.getId())             // user id
                .claim("email", user.getEmail())        // email
                .claim("phone", user.getPhone())        // phone
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(JWT_EXPIRATION_SECONDS)))
                .signWith(getSigningKey())               // HS256 по ключу
                .compact();
    }

    /**
     * Проверка валидности токена
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = (SecretKey) getSigningKey();
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Извлечение ID пользователя из токена
     */
    public UUID getUserIdFromToken(String token) {
        SecretKey key = (SecretKey) getSigningKey();
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String uidStr = claims.get("uid", String.class);
        return UUID.fromString(uidStr);
    }

   
    /**
     * Ключ подписи
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
