package com.peoplevoice.backend.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserProfileResponse user
) {
}
