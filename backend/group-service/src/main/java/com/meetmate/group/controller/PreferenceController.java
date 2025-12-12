package com.meetmate.group.controller;

import com.meetmate.group.dto.request.UpdatePreferenceRequest;
import com.meetmate.group.dto.response.MemberPreferenceResponse;
import com.meetmate.group.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups/{groupId}/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public MemberPreferenceResponse updatePreferences(@PathVariable UUID groupId,
                                                      @Valid @RequestBody UpdatePreferenceRequest request) {
        return preferenceService.updatePreferences(groupId, getCurrentUserId(), request);
    }

    @GetMapping
    public List<MemberPreferenceResponse> getGroupPreferences(@PathVariable UUID groupId) {
        return preferenceService.getGroupPreferences(groupId, getCurrentUserId());
    }

    @GetMapping("/{userId}")
    public MemberPreferenceResponse getMemberPreference(@PathVariable UUID groupId, @PathVariable UUID userId) {
        return preferenceService.getMemberPreference(groupId, getCurrentUserId(), userId);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request");
        }
        return UUID.fromString(authentication.getPrincipal().toString());
    }
}

