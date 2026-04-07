package com.peoplevoice.backend.repository;

import com.peoplevoice.backend.model.OfficerAvailability;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndActiveTrue(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleAndAvailability(Role role, OfficerAvailability availability);
}
