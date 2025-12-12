package com.meetmate.place.controller;

import com.meetmate.place.dto.request.GenerateRecommendationRequest;
import com.meetmate.place.dto.response.RecommendationResponse;
import com.meetmate.place.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/generate")
    public List<RecommendationResponse> generate(@Valid @RequestBody GenerateRecommendationRequest request) {
        UUID requesterId = getCurrentUserId();
        return recommendationService.generateRecommendations(requesterId, request);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request");
        }
        return UUID.fromString(authentication.getPrincipal().toString());
    }
}

