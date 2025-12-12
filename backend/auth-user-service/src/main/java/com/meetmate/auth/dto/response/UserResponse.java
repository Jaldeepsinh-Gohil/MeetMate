package com.meetmate.auth.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private String defaultLocation;
    private BigDecimal defaultLat;
    private BigDecimal defaultLng;
}

