package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.UserProfileResponse;
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

    public UserProfileResponse toProfile(User user) {
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole());
    }
}
