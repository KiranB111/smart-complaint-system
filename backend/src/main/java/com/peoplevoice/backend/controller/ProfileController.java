package com.peoplevoice.backend.controller;

import com.peoplevoice.backend.dto.CreateOfficerRequest;
import com.peoplevoice.backend.dto.UserProfileResponse;
import com.peoplevoice.backend.dto.OfficerAvailabilityUpdateRequest;
import com.peoplevoice.backend.model.OfficerAvailability;
import com.peoplevoice.backend.security.UserPrincipal;
import com.peoplevoice.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getProfile(principal.getUser().getId());
    }

    @GetMapping("/officers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> officers() {
        return userService.getOfficers();
    }

    @PutMapping("/me/availability")
    @PreAuthorize("hasRole('OFFICER')")
    public UserProfileResponse updateAvailability(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OfficerAvailabilityUpdateRequest request) {
        return userService.updateAvailability(
                principal.getUser().getId(),
                OfficerAvailability.valueOf(request.availability().toUpperCase())
        );
    }

    @PostMapping("/officers")
    @PreAuthorize("hasRole('ADMIN')")
    public UserProfileResponse createOfficer(@Valid @RequestBody CreateOfficerRequest request) {
        return userService.createOfficer(request);
    }
}
