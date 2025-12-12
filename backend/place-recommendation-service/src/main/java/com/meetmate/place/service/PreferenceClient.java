package com.meetmate.place.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * HTTP client to fetch preferences from group-service (via gateway or directly).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PreferenceClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${group.service.url:http://localhost:8082}")
    private String groupServiceUrl;

    public GroupPreferenceData fetchGroupPreferences(UUID groupId, UUID requesterId) {
        RestClient client = restClientBuilder.baseUrl(groupServiceUrl).build();
        List<PreferenceResponse> response = client.get()
            .uri("/api/groups/{groupId}/preferences", groupId)
            .header("X-User-Id", requesterId.toString())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.isEmpty()) {
            return new GroupPreferenceData(Map.of());
        }

        Map<UUID, MemberPref> prefs = response.stream()
            .collect(Collectors.toMap(
                PreferenceResponse::userId,
                pref -> MemberPref.builder()
                    .userId(pref.userId())
                    .budgetLevel(pref.budgetLevel())
                    .foodPreference(pref.foodPreference())
                    .maxDistanceKm(pref.maxDistanceKm())
                    .currentLat(pref.currentLat())
                    .currentLng(pref.currentLng())
                    .build()
            ));
        return new GroupPreferenceData(prefs);
    }

    /**
     * Data holder representing preferences returned by group-service.
     */
    public record GroupPreferenceData(Map<UUID, MemberPref> memberPreferences) {}

    @Builder
    public record MemberPref(
        UUID userId,
        String budgetLevel,
        String foodPreference,
        Integer maxDistanceKm,
        BigDecimal currentLat,
        BigDecimal currentLng
    ) {}

    // DTO for deserializing response from group-service
    public record PreferenceResponse(
        UUID id,
        UUID userId,
        String currentLocation,
        BigDecimal currentLat,
        BigDecimal currentLng,
        List<String> transportModes,
        Integer maxDistanceKm,
        String travelWillingness,
        String budgetLevel,
        String foodPreference
    ) {}
}

