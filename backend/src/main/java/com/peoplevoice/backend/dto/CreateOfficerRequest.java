package com.peoplevoice.backend.dto;

import com.peoplevoice.backend.model.OfficerAvailability;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOfficerRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Email String email,
        @NotBlank
        @Size(min = 8, max = 120)
        @Pattern(
                regexp = "^[A-Z](?=.*[^A-Za-z0-9]).{7,119}$",
                message = "must start with a capital letter, be at least 8 characters, and include 1 special character"
        ) String password,
        String phone,
        OfficerAvailability availability
) {
}
