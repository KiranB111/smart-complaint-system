package com.peoplevoice.backend.dto;

import com.peoplevoice.backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 120) String password,
        String phone,
        Role role
) {
}
