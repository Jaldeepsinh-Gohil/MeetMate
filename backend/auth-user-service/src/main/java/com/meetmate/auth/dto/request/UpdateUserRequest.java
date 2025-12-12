package com.meetmate.auth.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateUserRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String defaultLocation;

    private BigDecimal defaultLat;
    private BigDecimal defaultLng;
}

