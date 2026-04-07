package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.ComplaintRequest;
import com.peoplevoice.backend.dto.RegisterRequest;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final ComplaintService complaintService;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        Long citizenId = authService.register(new RegisterRequest(
                "Aarav Citizen",
                "citizen@peoplevoice.local",
                "password",
                "9999999999",
                Role.CITIZEN
        )).user().id();

        authService.register(new RegisterRequest(
                "Nisha Officer",
                "officer@peoplevoice.local",
                "password",
                "8888888888",
                Role.OFFICER
        ));

        authService.register(new RegisterRequest(
                "Admin User",
                "admin@peoplevoice.local",
                "password",
                "7777777777",
                Role.ADMIN
        ));

        complaintService.create(citizenId, new ComplaintRequest(
                "Overflowing garbage bin",
                "Garbage has not been collected for three days near the market road.",
                "Sanitation",
                "Market Road",
                12.9716,
                77.5946,
                "Bengaluru Central",
                null,
                null
        ));

        complaintService.create(citizenId, new ComplaintRequest(
                "Street light outage",
                "Two street lights are not working near the community park.",
                "Electrical",
                "Green Park Street",
                28.7041,
                77.1025,
                "Delhi Urban",
                null,
                null
        ));
    }
}
