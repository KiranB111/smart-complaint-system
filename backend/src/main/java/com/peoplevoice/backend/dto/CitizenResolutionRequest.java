package com.peoplevoice.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CitizenResolutionRequest(
        @NotNull Boolean resolved,
        @Min(1) @Max(5) Integer officerRating
) {
}
