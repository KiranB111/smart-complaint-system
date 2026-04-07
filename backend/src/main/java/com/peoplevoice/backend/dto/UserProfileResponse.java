package com.peoplevoice.backend.dto;

import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.OfficerAvailability;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String phone,
        Role role,
        OfficerAvailability availability,
        Double averageRating,
        Integer ratingCount
) {
}
