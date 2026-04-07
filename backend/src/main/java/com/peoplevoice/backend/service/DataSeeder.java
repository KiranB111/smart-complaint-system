package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.ComplaintRequest;
import com.peoplevoice.backend.dto.RegisterRequest;
import com.peoplevoice.backend.model.OfficerAvailability;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.User;
import com.peoplevoice.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final ComplaintService complaintService;
    private final PasswordEncoder passwordEncoder;

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

        createStaff("Nisha Officer", "officer@peoplevoice.local", "password", "8888888888", Role.OFFICER, OfficerAvailability.AVAILABLE);
        createStaff("Ravi Officer", "officer2@peoplevoice.local", "password", "8888888800", Role.OFFICER, OfficerAvailability.BUSY);
        createStaff("Meera Officer", "officer3@peoplevoice.local", "password", "8888888811", Role.OFFICER, OfficerAvailability.AVAILABLE);
        createStaff("Admin User", "admin@peoplevoice.local", "password", "7777777777", Role.ADMIN, OfficerAvailability.OFFLINE);

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

    private void createStaff(String name, String email, String password, String phone, Role role, OfficerAvailability availability) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setAvailability(availability);
        userRepository.save(user);
    }
}
