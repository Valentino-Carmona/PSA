package com.psa.proyecto_api.config;

import com.psa.proyecto_api.security.JwtUtil;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSecurityConfig {

    @Bean
    public RestTemplateCustomizer securityRestTemplateCustomizer(JwtUtil jwtUtil) {
        return restTemplate -> {
            String token = jwtUtil.generateToken("test-user");
            restTemplate.getInterceptors().add((request, body, execution) -> {
                // Agregar el header de autorización a TODAS las peticiones que hace TestRestTemplate
                request.getHeaders().set("Authorization", "Bearer " + token);
                return execution.execute(request, body);
            });
        };
    }
}
