package com.peoplevoice.backend.service;

import com.peoplevoice.backend.repository.UserRepository;
import com.peoplevoice.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> {
                    if (!user.isActive()) {
                        throw new DisabledException("User account is deactivated");
                    }
                    return new UserPrincipal(user);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
