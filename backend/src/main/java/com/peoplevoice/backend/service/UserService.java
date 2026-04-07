package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.UserProfileResponse;
import com.peoplevoice.backend.model.OfficerAvailability;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.User;
import com.peoplevoice.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile(Long id) {
        return userRepository.findById(id)
                .map(this::toProfile)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public java.util.List<UserProfileResponse> getOfficers() {
        return userRepository.findByRole(Role.OFFICER).stream().map(this::toProfile).toList();
    }

    public UserProfileResponse updateAvailability(Long userId, OfficerAvailability availability) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getRole() != Role.OFFICER) {
            throw new IllegalArgumentException("Only officers can update availability");
        }
        user.setAvailability(availability);
        return toProfile(userRepository.save(user));
    }

    public UserProfileResponse toProfile(User user) {
        double averageRating = user.getRatingCount() == null || user.getRatingCount() == 0
                ? 0.0
                : (double) user.getTotalRatingScore() / user.getRatingCount();
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getAvailability(),
                averageRating,
                user.getRatingCount()
        );
    }
}
