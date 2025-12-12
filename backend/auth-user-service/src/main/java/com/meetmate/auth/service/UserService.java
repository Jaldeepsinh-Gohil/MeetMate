package com.meetmate.auth.service;

import com.meetmate.auth.dto.request.UpdateUserRequest;
import com.meetmate.auth.dto.response.UserResponse;
import com.meetmate.auth.entity.User;
import com.meetmate.auth.exception.UserNotFoundException;
import com.meetmate.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUserProfile(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getDefaultLocation() != null) {
            user.setDefaultLocation(request.getDefaultLocation());
        }
        if (request.getDefaultLat() != null) {
            user.setDefaultLat(request.getDefaultLat());
        }
        if (request.getDefaultLng() != null) {
            user.setDefaultLng(request.getDefaultLng());
        }

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .defaultLocation(user.getDefaultLocation())
            .defaultLat(user.getDefaultLat())
            .defaultLng(user.getDefaultLng())
            .build();
    }
}

