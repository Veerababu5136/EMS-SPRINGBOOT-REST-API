package com.restapi.eventManagementSystem.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.restapi.eventManagementSystem.entites.User;
import com.restapi.eventManagementSystem.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Fetch user by email or throw exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User email not found: " + email));

        // 2. Ensure the role is not null or empty
        String role = user.getROLE();
        if (role == null || role.isEmpty()) {
            throw new UsernameNotFoundException("User has no assigned role.");
        }

        // 3. Prefix role with "ROLE_" if not already
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }

        // 4. Return UserDetails with authorities
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                userAuthorities(Set.of(role))
        );
    }

    // Helper method to convert roles into GrantedAuthority
    private Collection<? extends GrantedAuthority> userAuthorities(Set<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
