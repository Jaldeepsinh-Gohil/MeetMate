package com.meetmate.place.service;

import com.meetmate.place.dto.request.GenerateRecommendationRequest;
import com.meetmate.place.dto.response.RecommendationResponse;
import com.meetmate.place.entity.Place;
import com.meetmate.place.entity.Recommendation;
import com.meetmate.place.exception.NotFoundException;
import com.meetmate.place.repository.PlaceRepository;
import com.meetmate.place.repository.RecommendationRepository;
import com.meetmate.place.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final PlaceRepository placeRepository;
    private final RecommendationRepository recommendationRepository;
    private final PreferenceClient preferenceClient;

    @Transactional
    public List<RecommendationResponse> generateRecommendations(UUID requesterId, GenerateRecommendationRequest request) {
        UUID groupId = request.getGroupId();

        PreferenceClient.GroupPreferenceData prefs = preferenceClient.fetchGroupPreferences(groupId, requesterId);
        Map<UUID, PreferenceClient.MemberPref> memberPrefs = prefs.memberPreferences();
        if (memberPrefs.isEmpty()) {
            throw new NotFoundException("No member preferences found for group");
        }

        Set<UUID> targetMembers = request.getMemberIds() == null || request.getMemberIds().isEmpty()
            ? memberPrefs.keySet()
            : memberPrefs.keySet().stream()
                .filter(id -> request.getMemberIds().contains(id))
                .collect(Collectors.toSet());

        List<Place> places = placeRepository.searchActive(null, null, null);
        List<RecommendationScore> scored = new ArrayList<>();

        for (Place place : places) {
            ScoreResult result = scorePlace(place, targetMembers, memberPrefs);
            if (result == null) continue; // filtered out by constraints

            RecommendationScore score = new RecommendationScore(place, result.score(), result.avgDistance(), result.maxDistance(), result.reasoning());
            scored.add(score);
        }

        scored.sort(Comparator.comparing(RecommendationScore::score).reversed());
        int limit = request.getMaxResults() != null ? request.getMaxResults() : 5;
        List<RecommendationScore> top = scored.stream().limit(limit).toList();

        List<RecommendationResponse> responses = top.stream().map(rs -> saveAndMap(groupId, requesterId, targetMembers, rs)).toList();
        return responses;
    }

    private RecommendationResponse saveAndMap(UUID groupId, UUID requesterId, Set<UUID> memberIds, RecommendationScore rs) {
        Recommendation rec = Recommendation.builder()
            .groupId(groupId)
            .place(rs.place())
            .requestedBy(requesterId)
            .memberIds(new ArrayList<>(memberIds))
            .score(BigDecimal.valueOf(rs.score()).setScale(2, RoundingMode.HALF_UP))
            .avgDistanceKm(BigDecimal.valueOf(rs.avgDistance()).setScale(2, RoundingMode.HALF_UP))
            .maxDistanceKm(BigDecimal.valueOf(rs.maxDistance()).setScale(2, RoundingMode.HALF_UP))
            .reasoning(rs.reasoning())
            .build();
        Recommendation saved = recommendationRepository.save(rec);
        return RecommendationResponse.builder()
            .recommendationId(saved.getId())
            .placeId(saved.getPlace().getId())
            .placeName(saved.getPlace().getName())
            .category(saved.getPlace().getCategory())
            .area(saved.getPlace().getArea())
            .costLevel(saved.getPlace().getCostLevel())
            .hasVeg(saved.getPlace().isHasVeg())
            .hasNonVeg(saved.getPlace().isHasNonVeg())
            .rating(saved.getPlace().getRating())
            .score(saved.getScore())
            .avgDistanceKm(saved.getAvgDistanceKm())
            .maxDistanceKm(saved.getMaxDistanceKm())
            .reasoning(saved.getReasoning())
            .memberIds(saved.getMemberIds())
            .build();
    }

    private record ScoreResult(double score, double avgDistance, double maxDistance, String reasoning) {}
    private record RecommendationScore(Place place, double score, double avgDistance, double maxDistance, String reasoning) {}

    private ScoreResult scorePlace(Place place, Set<UUID> memberIds, Map<UUID, PreferenceClient.MemberPref> prefs) {
        List<Double> distances = new ArrayList<>();
        List<String> reasons = new ArrayList<>();

        double maxAllowedDistance = 50.0; // default cap
        int minBudgetRank = Integer.MAX_VALUE; // 0=LOW,1=MEDIUM,2=HIGH
        boolean requiresVegOnly = false;

        for (UUID memberId : memberIds) {
            PreferenceClient.MemberPref pref = prefs.get(memberId);
            if (pref == null || pref.currentLat() == null || pref.currentLng() == null) {
                continue;
            }
            double dist = DistanceCalculator.calculateDistance(
                pref.currentLat(), pref.currentLng(),
                place.getLat().doubleValue(), place.getLng().doubleValue()
            );
            distances.add(dist);

            if (pref.maxDistanceKm() != null) {
                maxAllowedDistance = Math.min(maxAllowedDistance, pref.maxDistanceKm());
            }
            minBudgetRank = Math.min(minBudgetRank, budgetRank(pref.budgetLevel()));
            if ("VEG_ONLY".equals(pref.foodPreference())) {
                requiresVegOnly = true;
            }
        }

        if (distances.isEmpty()) {
            return null;
        }

        double maxDistance = distances.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        if (maxDistance > maxAllowedDistance) {
            return null; // fails distance constraint
        }

        if (requiresVegOnly && !place.isHasVeg()) {
            return null; // fails veg-only constraint
        }

        int placeBudgetRank = budgetRank(place.getCostLevel());
        if (placeBudgetRank > minBudgetRank) {
            return null; // fails budget constraint
        }

        DoubleSummaryStatistics stats = distances.stream().mapToDouble(Double::doubleValue).summaryStatistics();
        double avg = stats.getAverage();
        double stddev = calculateStdDev(distances, avg);

        double distanceFairness = Math.max(0, 100 - (stddev / 50.0 * 100)); // normalized
        double avgDistanceScore = Math.max(0, 100 - (avg / 50.0 * 100));
        double budgetScore = budgetScore(placeBudgetRank, minBudgetRank);
        double ratingScore = place.getRating() != null ? (place.getRating().doubleValue() / 5.0) * 100 : 50;

        double finalScore = (distanceFairness * 0.4) + (avgDistanceScore * 0.3) + (budgetScore * 0.15) + (ratingScore * 0.15);

        reasons.add(String.format("Fairness: stddev %.2f km", stddev));
        reasons.add(String.format("Avg distance: %.2f km", avg));
        reasons.add(String.format("Budget: place %s vs group min %s", rankLabel(placeBudgetRank), rankLabel(minBudgetRank)));
        reasons.add(String.format("Rating: %s", place.getRating() != null ? place.getRating() : "N/A"));

        String reasoning = String.join("; ", reasons);
        return new ScoreResult(finalScore, avg, maxDistance, reasoning);
    }

    private int budgetRank(String level) {
        if (level == null) return 2;
        return switch (level.toUpperCase()) {
            case "LOW" -> 0;
            case "MEDIUM" -> 1;
            default -> 2;
        };
    }

    private double budgetScore(int placeRank, int minRank) {
        return placeRank <= minRank ? 100 : Math.max(0, 100 - (placeRank - minRank) * 50.0);
    }

    private String rankLabel(int rank) {
        return switch (rank) {
            case 0 -> "LOW";
            case 1 -> "MEDIUM";
            default -> "HIGH";
        };
    }

    private double calculateStdDev(List<Double> values, double mean) {
        if (values.size() <= 1) return 0;
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0);
        return Math.sqrt(variance);
    }
}

