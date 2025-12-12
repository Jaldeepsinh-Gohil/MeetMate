package com.meetmate.auth.util;

import com.meetmate.auth.config.JwtConfig;
import com.meetmate.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user) {
        return buildToken(user, jwtConfig.getAccessTokenExpiry(), TYPE_ACCESS);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, jwtConfig.getRefreshTokenExpiry(), TYPE_REFRESH);
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, TYPE_ACCESS);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, TYPE_REFRESH);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_EMAIL, String.class));
    }

    private boolean validateToken(String token, String requiredType) {
        try {
            Jws<Claims> parsed = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            Claims claims = parsed.getPayload();
            String tokenType = claims.get(CLAIM_TYPE, String.class);
            Date expiration = claims.getExpiration();
            return requiredType.equals(tokenType) && expiration.after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    private String buildToken(User user, long expiryMillis, String type) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMillis);

        return Jwts.builder()
            .subject(user.getId().toString())
            .claims(Map.of(
                CLAIM_EMAIL, user.getEmail(),
                CLAIM_TYPE, type
            ))
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey())
            .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claimsResolver.apply(claims);
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}

