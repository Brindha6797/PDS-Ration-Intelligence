package com.wipro.pds.service;

import com.wipro.pds.dto.AuthRequest;
import com.wipro.pds.dto.AuthResponse;
import com.wipro.pds.repository.UserRepository;
import com.wipro.pds.security.JwtTokenProvider;
import com.wipro.pds.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Update last login timestamp
        userRepository.findById(principal.getUserId()).ifPresent(u -> {
            u.setLastLoginAt(LocalDateTime.now());
            userRepository.save(u);
        });

        List<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return new AuthResponse(
            jwt,
            principal.getUsername(),
            principal.getFullName(),
            roles,
            principal.getAssignedShopId(),
            principal.getAssignedGodownId()
        );
    }
}
