package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.UserProfileResponse;
import com.peoplevoice.backend.dto.CreateOfficerRequest;
import com.peoplevoice.backend.dto.UpdateOfficerRequest;
import com.peoplevoice.backend.model.ComplaintStatus;
import com.peoplevoice.backend.model.OfficerAvailability;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.User;
import com.peoplevoice.backend.repository.ComplaintRepository;
import com.peoplevoice.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;

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
        if (!user.isActive()) {
            throw new IllegalArgumentException("Deactivated officers cannot update availability");
        }
        user.setAvailability(availability);
        return toProfile(userRepository.save(user));
    }

    public UserProfileResponse createOfficer(CreateOfficerRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new IllegalArgumentException("Email already registered");
        });
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(Role.OFFICER);
        user.setAvailability(request.availability() == null ? OfficerAvailability.AVAILABLE : request.availability());
        user.setActive(true);
        return toProfile(userRepository.save(user));
    }

    public UserProfileResponse updateOfficer(Long officerId, UpdateOfficerRequest request) {
        User officer = userRepository.findById(officerId)
                .filter(user -> user.getRole() == Role.OFFICER)
                .orElseThrow(() -> new EntityNotFoundException("Officer not found"));

        userRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(officerId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already registered");
                });

        officer.setName(request.name());
        officer.setEmail(request.email());
        officer.setPhone(request.phone());
        officer.setAvailability(request.availability() == null ? officer.getAvailability() : request.availability());
        if (request.active() != null && !request.active().equals(officer.isActive())) {
            setOfficerActiveState(officer, request.active());
        }
        return toProfile(userRepository.save(officer));
    }

    public UserProfileResponse setOfficerActive(Long officerId, boolean active) {
        User officer = userRepository.findById(officerId)
                .filter(user -> user.getRole() == Role.OFFICER)
                .orElseThrow(() -> new EntityNotFoundException("Officer not found"));
        setOfficerActiveState(officer, active);
        return toProfile(userRepository.save(officer));
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
                user.getRatingCount(),
                user.isActive()
        );
    }

    private void setOfficerActiveState(User officer, boolean active) {
        if (!active) {
            long activeAssignments = complaintRepository.countByAssignedOfficerIdAndStatusIn(
                    officer.getId(),
                    java.util.List.of(ComplaintStatus.ASSIGNED, ComplaintStatus.IN_PROGRESS, ComplaintStatus.PENDING_CITIZEN_CONFIRMATION)
            );
            if (activeAssignments > 0) {
                throw new IllegalArgumentException("Officer has active complaints and cannot be deactivated");
            }
            officer.setAvailability(OfficerAvailability.OFFLINE);
        }
        officer.setActive(active);
    }
}
