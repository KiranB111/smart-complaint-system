package com.peoplevoice.backend.dto;

import com.peoplevoice.backend.model.Role;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String phone,
        Role role
) {
}
