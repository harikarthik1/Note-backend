package com.notes.service;

import com.notes.dto.AuthResponse;
import com.notes.dto.LoginRequest;
import com.notes.dto.RegisterRequest;
import com.notes.model.RefreshToken;
import com.notes.model.User;
import com.notes.repository.RefreshTokenRepository;
import com.notes.repository.UserRepository;
import com.notes.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshRepo;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return generateTokens(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return generateTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {

        RefreshToken storedToken = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshRepo.delete(storedToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return generateTokens(user);
    }

    private AuthResponse generateTokens(User user) {

        String accessToken = jwtService.generateToken(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        refreshRepo.deleteByUserId(user.getId());

        refreshRepo.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)) // 7 days
                        .build()
        );

        return new AuthResponse(accessToken, refreshToken);
    }
}