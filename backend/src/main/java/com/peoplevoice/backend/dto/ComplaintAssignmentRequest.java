package com.peoplevoice.backend.dto;

import jakarta.validation.constraints.NotNull;

public record ComplaintAssignmentRequest(@NotNull Long officerId) {
}
