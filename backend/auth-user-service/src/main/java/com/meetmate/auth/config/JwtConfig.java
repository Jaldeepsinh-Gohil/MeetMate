package com.meetmate.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    /**
     * HMAC secret key used for signing JWTs. Should be at least 256 bits.
     */
    private String secret;
    /**
     * Access token expiration in milliseconds.
     */
    private long accessTokenExpiry;
    /**
     * Refresh token expiration in milliseconds.
     */
    private long refreshTokenExpiry;
}

