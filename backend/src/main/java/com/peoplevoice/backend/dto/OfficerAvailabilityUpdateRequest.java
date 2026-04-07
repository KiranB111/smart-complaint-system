package com.peoplevoice.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record OfficerAvailabilityUpdateRequest(@NotBlank String availability) {
}
