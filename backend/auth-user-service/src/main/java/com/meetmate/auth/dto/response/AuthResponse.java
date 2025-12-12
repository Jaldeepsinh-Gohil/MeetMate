package com.meetmate.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
}

