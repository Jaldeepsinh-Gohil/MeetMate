package com.meetmate.auth.service;

import com.meetmate.auth.dto.request.LoginRequest;
import com.meetmate.auth.dto.request.RefreshTokenRequest;
import com.meetmate.auth.dto.request.RegisterRequest;
import com.meetmate.auth.dto.response.AuthResponse;
import com.meetmate.auth.dto.response.UserResponse;
import com.meetmate.auth.entity.User;
import com.meetmate.auth.exception.InvalidCredentialsException;
import com.meetmate.auth.exception.UserAlreadyExistsException;
import com.meetmate.auth.repository.UserRepository;
import com.meetmate.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = User.builder()
            .email(request.getEmail().toLowerCase())
            .name(request.getName())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .build();

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!jwtUtil.validateRefreshToken(token)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        UUID userId = UUID.fromString(jwtUtil.extractUserId(token));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        UserResponse userResponse = UserService.toResponse(user);
        return AuthResponse.builder()
            .user(userResponse)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}

