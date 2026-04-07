package com.peoplevoice.backend.dto;

import com.peoplevoice.backend.model.OfficerAvailability;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOfficerRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Email String email,
        String phone,
        OfficerAvailability availability,
        Boolean active
) {
}
