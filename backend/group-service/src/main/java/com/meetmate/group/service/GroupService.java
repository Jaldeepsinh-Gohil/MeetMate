package com.meetmate.group.service;

import com.meetmate.group.dto.request.AddMemberRequest;
import com.meetmate.group.dto.request.CreateGroupRequest;
import com.meetmate.group.dto.request.UpdateGroupRequest;
import com.meetmate.group.dto.response.GroupMemberResponse;
import com.meetmate.group.dto.response.GroupResponse;
import com.meetmate.group.entity.Group;
import com.meetmate.group.entity.GroupMember;
import com.meetmate.group.exception.ForbiddenOperationException;
import com.meetmate.group.exception.GroupNotFoundException;
import com.meetmate.group.exception.MemberNotFoundException;
import com.meetmate.group.exception.ValidationException;
import com.meetmate.group.repository.GroupMemberRepository;
import com.meetmate.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, UUID ownerId) {
        Group group = Group.builder()
            .name(request.getName())
            .ownerId(ownerId)
            .build();
        Group saved = groupRepository.save(group);

        GroupMember ownerMembership = GroupMember.builder()
            .group(saved)
            .userId(ownerId)
            .nickname(null)
            .isActive(true)
            .build();
        groupMemberRepository.save(ownerMembership);

        return toResponse(saved, List.of(toResponse(ownerMembership)));
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> listGroups(UUID userId) {
        return groupRepository.findAllForUser(userId).stream()
            .map(group -> toResponse(group, groupMemberRepository.findAllByGroupId(group.getId())
                .stream().map(this::toResponse).toList()))
            .toList();
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureMember(group, userId);
        List<GroupMemberResponse> members = groupMemberRepository.findAllByGroupId(groupId)
            .stream().map(this::toResponse).toList();
        return toResponse(group, members);
    }

    @Transactional
    public GroupResponse updateGroup(UUID groupId, UpdateGroupRequest request, UUID userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureOwner(group, userId);
        group.setName(request.getName());
        Group saved = groupRepository.save(group);
        List<GroupMemberResponse> members = groupMemberRepository.findAllByGroupId(groupId)
            .stream().map(this::toResponse).toList();
        return toResponse(saved, members);
    }

    @Transactional
    public void deleteGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureOwner(group, userId);
        groupRepository.delete(group);
    }

    @Transactional
    public GroupResponse addMember(UUID groupId, AddMemberRequest request, UUID userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureOwner(group, userId);

        UUID newUserId = UUID.fromString(request.getUserId());
        groupMemberRepository.findByGroupAndUserId(group, newUserId).ifPresent(existing -> {
            if (existing.isActive()) {
                throw new ValidationException("User already a member");
            } else {
                existing.setActive(true);
                existing.setNickname(request.getNickname());
                groupMemberRepository.save(existing);
            }
        });

        if (!groupMemberRepository.existsByGroupAndUserId(group, newUserId)) {
            GroupMember member = GroupMember.builder()
                .group(group)
                .userId(newUserId)
                .nickname(request.getNickname())
                .isActive(true)
                .build();
            groupMemberRepository.save(member);
        }

        List<GroupMemberResponse> members = groupMemberRepository.findAllByGroupId(groupId)
            .stream().map(this::toResponse).toList();
        return toResponse(group, members);
    }

    @Transactional
    public void removeMember(UUID groupId, UUID memberUserId, UUID requesterId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        ensureOwner(group, requesterId);

        GroupMember member = groupMemberRepository.findByGroupAndUserId(group, memberUserId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found in group"));

        member.setActive(false);
        groupMemberRepository.save(member);
    }

    private void ensureMember(Group group, UUID userId) {
        boolean isMember = groupMemberRepository.existsByGroupAndUserId(group, userId);
        if (!isMember) {
            throw new ForbiddenOperationException("You are not a member of this group");
        }
    }

    private void ensureOwner(Group group, UUID userId) {
        if (!group.getOwnerId().equals(userId)) {
            throw new ForbiddenOperationException("Only owner can modify the group");
        }
    }

    private GroupResponse toResponse(Group group, List<GroupMemberResponse> members) {
        return GroupResponse.builder()
            .id(group.getId())
            .name(group.getName())
            .ownerId(group.getOwnerId())
            .createdAt(group.getCreatedAt())
            .members(members)
            .build();
    }

    private GroupMemberResponse toResponse(GroupMember member) {
        return GroupMemberResponse.builder()
            .id(member.getId())
            .userId(member.getUserId())
            .nickname(member.getNickname())
            .active(member.isActive())
            .joinedAt(member.getJoinedAt())
            .build();
    }
}

