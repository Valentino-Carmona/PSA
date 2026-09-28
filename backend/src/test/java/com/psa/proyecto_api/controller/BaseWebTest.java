package com.psa.proyecto_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psa.proyecto_api.repository.UserRepository;
import com.psa.proyecto_api.security.JwtAuthenticationEntryPoint;
import com.psa.proyecto_api.security.JwtAuthenticationFilter;
import com.psa.proyecto_api.security.JwtUtil;
import com.psa.proyecto_api.security.RateLimitingFilter;
import com.psa.proyecto_api.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseWebTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtUtil jwtUtil;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
}
