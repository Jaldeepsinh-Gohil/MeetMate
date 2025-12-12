package com.meetmate.group.dto.response;

import com.meetmate.group.enums.BudgetLevel;
import com.meetmate.group.enums.FoodPreference;
import com.meetmate.group.enums.TransportMode;
import com.meetmate.group.enums.TravelWillingness;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class MemberPreferenceResponse {
    private UUID id;
    private UUID userId;
    private String currentLocation;
    private BigDecimal currentLat;
    private BigDecimal currentLng;
    private Set<TransportMode> transportModes;
    private Integer maxDistanceKm;
    private TravelWillingness travelWillingness;
    private BudgetLevel budgetLevel;
    private FoodPreference foodPreference;
    private LocalDateTime updatedAt;
}

