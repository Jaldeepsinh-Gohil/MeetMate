package com.meetmate.place.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PlaceResponse {
    private UUID id;
    private String name;
    private String category;
    private String area;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String costLevel;
    private boolean hasVeg;
    private boolean hasNonVeg;
    private BigDecimal rating;
    private boolean active;
}

