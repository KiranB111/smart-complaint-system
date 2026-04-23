package com.peoplevoice.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComplaintRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 1000) String description,
        @NotBlank @Size(max = 80) String category,
        @NotBlank @Size(max = 120) String location,
        Double latitude,
        Double longitude,
        @NotBlank @Size(max = 120) String locality,
        String status,
        Long assignedOfficerId
) {
}
