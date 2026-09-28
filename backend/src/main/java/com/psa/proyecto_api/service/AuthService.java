package com.psa.proyecto_api.service;

import com.psa.proyecto_api.dto.request.LoginRequest;
import com.psa.proyecto_api.dto.response.AuthResponse;
import com.psa.proyecto_api.model.User;
import com.psa.proyecto_api.repository.UserRepository;
import com.psa.proyecto_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token);
    }
}
