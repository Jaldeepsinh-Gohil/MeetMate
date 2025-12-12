package com.meetmate.group.service;

import com.meetmate.group.dto.request.UpdatePreferenceRequest;
import com.meetmate.group.dto.response.MemberPreferenceResponse;
import com.meetmate.group.entity.Group;
import com.meetmate.group.entity.GroupMember;
import com.meetmate.group.entity.MemberPreference;
import com.meetmate.group.exception.ForbiddenOperationException;
import com.meetmate.group.exception.GroupNotFoundException;
import com.meetmate.group.exception.ValidationException;
import com.meetmate.group.repository.GroupMemberRepository;
import com.meetmate.group.repository.GroupRepository;
import com.meetmate.group.repository.MemberPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberPreferenceRepository memberPreferenceRepository;

    @Transactional
    public MemberPreferenceResponse updatePreferences(UUID groupId, UUID userId, UpdatePreferenceRequest request) {
        validatePreferenceRequest(request);
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureMember(group, userId);

        MemberPreference preference = memberPreferenceRepository.findByGroupAndUserId(group, userId)
            .orElse(MemberPreference.builder().group(group).userId(userId).build());

        preference.setCurrentLocation(request.getCurrentLocation());
        preference.setCurrentLat(request.getCurrentLat());
        preference.setCurrentLng(request.getCurrentLng());
        preference.setTransportModes(request.getTransportModes());
        preference.setMaxDistanceKm(request.getMaxDistanceKm());
        preference.setTravelWillingness(request.getTravelWillingness());
        preference.setBudgetLevel(request.getBudgetLevel());
        preference.setFoodPreference(request.getFoodPreference());

        MemberPreference saved = memberPreferenceRepository.save(preference);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MemberPreferenceResponse> getGroupPreferences(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureMember(group, userId);
        return memberPreferenceRepository.findAllByGroupId(groupId)
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MemberPreferenceResponse getMemberPreference(UUID groupId, UUID requesterId, UUID memberId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureMember(group, requesterId);
        MemberPreference preference = memberPreferenceRepository.findByGroupAndUserId(group, memberId)
            .orElseThrow(() -> new GroupNotFoundException("Member preference not found"));
        return toResponse(preference);
    }

    private void validatePreferenceRequest(UpdatePreferenceRequest request) {
        if (request.getTransportModes() == null || request.getTransportModes().isEmpty()) {
            throw new ValidationException("At least one transport mode is required");
        }
        if (request.getMaxDistanceKm() != null &&
            (request.getMaxDistanceKm() < 1 || request.getMaxDistanceKm() > 50)) {
            throw new ValidationException("maxDistanceKm must be between 1 and 50");
        }
    }

    private void ensureMember(Group group, UUID userId) {
        boolean isMember = groupMemberRepository.existsByGroupAndUserId(group, userId);
        if (!isMember) {
            throw new ForbiddenOperationException("You are not a member of this group");
        }
    }

    private MemberPreferenceResponse toResponse(MemberPreference preference) {
        return MemberPreferenceResponse.builder()
            .id(preference.getId())
            .userId(preference.getUserId())
            .currentLocation(preference.getCurrentLocation())
            .currentLat(preference.getCurrentLat())
            .currentLng(preference.getCurrentLng())
            .transportModes(preference.getTransportModes())
            .maxDistanceKm(preference.getMaxDistanceKm())
            .travelWillingness(preference.getTravelWillingness())
            .budgetLevel(preference.getBudgetLevel())
            .foodPreference(preference.getFoodPreference())
            .updatedAt(preference.getUpdatedAt())
            .build();
    }
}

