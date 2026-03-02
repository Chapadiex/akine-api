package com.akine_api.infrastructure.security;

import com.akine_api.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceAdapter implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    public UserDetailsServiceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var entity = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        var authorities = entity.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toList());

        return User.builder()
                .username(entity.getEmail())
                .password(entity.getPasswordHash())
                .authorities(authorities)
                .accountLocked("SUSPENDED".equals(entity.getStatus()))
                .build();
    }
}
