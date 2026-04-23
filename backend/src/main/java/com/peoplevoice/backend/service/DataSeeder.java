package com.peoplevoice.backend.service;

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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureStaff("Kiran", "admin@peoplevoice.local", "password", "7777777777", Role.ADMIN, OfficerAvailability.OFFLINE);
    }

    private void ensureStaff(String name, String email, String password, String phone, Role role, OfficerAvailability availability) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setName(name);
        user.setEmail(email);
        boolean isSeededAdmin = role == Role.ADMIN && "admin@peoplevoice.local".equalsIgnoreCase(email);
        if (isSeededAdmin || user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setPhone(phone);
        user.setRole(role);
        user.setActive(true);
        user.setAvailability(availability);
        userRepository.save(user);
    }
}
