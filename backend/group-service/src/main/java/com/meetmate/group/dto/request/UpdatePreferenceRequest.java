package com.meetmate.group.dto.request;

import com.meetmate.group.enums.BudgetLevel;
import com.meetmate.group.enums.FoodPreference;
import com.meetmate.group.enums.TransportMode;
import com.meetmate.group.enums.TravelWillingness;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class UpdatePreferenceRequest {

    @Size(max = 255)
    private String currentLocation;

    private BigDecimal currentLat;
    private BigDecimal currentLng;

    @NotEmpty(message = "At least one transport mode is required")
    private Set<TransportMode> transportModes;

    @Min(value = 1, message = "maxDistanceKm must be at least 1")
    @Max(value = 50, message = "maxDistanceKm must be at most 50")
    private Integer maxDistanceKm;

    private TravelWillingness travelWillingness;

    private BudgetLevel budgetLevel;

    private FoodPreference foodPreference;
}

