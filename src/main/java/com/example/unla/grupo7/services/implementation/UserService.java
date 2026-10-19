package com.example.unla.grupo7.services.implementation;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.entities.User;
import com.example.unla.grupo7.repositories.IUserRepository;

@Service
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Usuario no encontrado: " + username
                    )
                );

        String[] authorities = user.getUserRoles()
                .stream()
                .map(userRole -> userRole.getRole())
                .map(role -> role.startsWith("ROLE_")
                        ? role
                        : "ROLE_" + role)
                .distinct()
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(authorities)
                .build();
    }
}
