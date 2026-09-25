package com.psa.proyecto_api.controller;

import com.psa.proyecto_api.dto.request.LoginRequest;
import com.psa.proyecto_api.dto.response.AuthResponse;
import com.psa.proyecto_api.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // En una aplicación de portfolio simple, validamos credenciales hardcodeadas
        // o emitimos un token confiando en el request.
        // Simularemos que cualquier credencial es válida para emitir el token y poder testear.
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
