package com.meetmate.place.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GenerateRecommendationRequest {
    @NotNull
    private UUID groupId;

    private List<UUID> memberIds;

    @Min(1)
    @Max(20)
    private Integer maxResults = 5;
}

