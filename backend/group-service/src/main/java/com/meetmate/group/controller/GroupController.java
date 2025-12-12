package com.meetmate.group.controller;

import com.meetmate.group.dto.request.AddMemberRequest;
import com.meetmate.group.dto.request.CreateGroupRequest;
import com.meetmate.group.dto.request.UpdateGroupRequest;
import com.meetmate.group.dto.response.GroupResponse;
import com.meetmate.group.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request, getCurrentUserId());
    }

    @GetMapping
    public List<GroupResponse> listGroups() {
        return groupService.listGroups(getCurrentUserId());
    }

    @GetMapping("/{id}")
    public GroupResponse getGroup(@PathVariable UUID id) {
        return groupService.getGroup(id, getCurrentUserId());
    }

    @PutMapping("/{id}")
    public GroupResponse updateGroup(@PathVariable UUID id, @Valid @RequestBody UpdateGroupRequest request) {
        return groupService.updateGroup(id, request, getCurrentUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id, getCurrentUserId());
    }

    @PostMapping("/{id}/members")
    public GroupResponse addMember(@PathVariable UUID id, @Valid @RequestBody AddMemberRequest request) {
        return groupService.addMember(id, request, getCurrentUserId());
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable UUID groupId, @PathVariable UUID userId) {
        groupService.removeMember(groupId, userId, getCurrentUserId());
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request");
        }
        return UUID.fromString(authentication.getPrincipal().toString());
    }
}

