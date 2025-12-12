package com.meetmate.place.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RecommendationResponse {
    private UUID recommendationId;
    private UUID placeId;
    private String placeName;
    private String category;
    private String area;
    private String costLevel;
    private boolean hasVeg;
    private boolean hasNonVeg;
    private BigDecimal rating;
    private BigDecimal score;
    private BigDecimal avgDistanceKm;
    private BigDecimal maxDistanceKm;
    private String reasoning;
    private List<UUID> memberIds;
}

