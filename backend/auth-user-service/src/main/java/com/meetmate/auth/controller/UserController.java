package com.meetmate.auth.controller;

import com.meetmate.auth.dto.request.UpdateUserRequest;
import com.meetmate.auth.dto.response.UserResponse;
import com.meetmate.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me() {
        UUID userId = getCurrentUserId();
        return userService.getUserProfile(userId);
    }

    @PutMapping("/me")
    public UserResponse updateProfile(@Valid @RequestBody UpdateUserRequest request) {
        UUID userId = getCurrentUserId();
        return userService.updateUserProfile(userId, request);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request");
        }
        return UUID.fromString(authentication.getPrincipal().toString());
    }
}

