package com.psa.proyecto_api.service;

import com.psa.proyecto_api.dto.request.LoginRequest;
import com.psa.proyecto_api.dto.response.AuthResponse;
import com.psa.proyecto_api.model.User;
import com.psa.proyecto_api.repository.UserRepository;
import com.psa.proyecto_api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
                
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyString())).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@test.com");
        verify(jwtUtil).generateToken("test@test.com");
    }
}
