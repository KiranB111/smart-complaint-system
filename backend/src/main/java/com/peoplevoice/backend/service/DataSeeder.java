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
        Long citizenId = ensureCitizen(
                "Aarav Citizen",
                "citizen@peoplevoice.local",
                "password",
                "9999999999"
        );

        ensureStaff("Kiran", "admin@peoplevoice.local", "password", "7777777777", Role.ADMIN, OfficerAvailability.OFFLINE);

        ensureStaff("Pradeep", "pradeep@peoplevoice.local", "password", "8888888801", Role.OFFICER, OfficerAvailability.AVAILABLE);
        ensureStaff("Rajesh", "rajesh@peoplevoice.local", "password", "8888888802", Role.OFFICER, OfficerAvailability.BUSY);
        ensureStaff("Chaitanya", "chaitanya@peoplevoice.local", "password", "8888888803", Role.OFFICER, OfficerAvailability.AVAILABLE);
        ensureStaff("Nagur", "nagur@peoplevoice.local", "password", "8888888804", Role.OFFICER, OfficerAvailability.OFFLINE);
        ensureStaff("Vinesh", "vinesh@peoplevoice.local", "password", "8888888805", Role.OFFICER, OfficerAvailability.AVAILABLE);
        ensureStaff("Jayaram", "jayaram@peoplevoice.local", "password", "8888888806", Role.OFFICER, OfficerAvailability.BUSY);
        ensureStaff("Ramu", "ramu@peoplevoice.local", "password", "8888888807", Role.OFFICER, OfficerAvailability.AVAILABLE);

        if (complaintService.findForCitizen(citizenId, null, null).isEmpty()) {
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

    private Long ensureCitizen(String name, String email, String password, String phone) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseGet(() -> authService.register(new RegisterRequest(
                        name,
                        email,
                        password,
                        phone,
                        Role.CITIZEN
                )).user().id());
    }

    private void ensureStaff(String name, String email, String password, String phone, Role role, OfficerAvailability availability) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setName(name);
        user.setEmail(email);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setPhone(phone);
        user.setRole(role);
        user.setAvailability(availability);
        userRepository.save(user);
    }
}
