package com.psa.proyecto_api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = "psa.ratelimit.capacity=5")
public class SecurityEdgeCaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void whenInvalidTokenSignature_thenReturns401() throws Exception {
        // Generar un token con otra llave (falsificado)
        String fakeToken = Jwts.builder()
                .setSubject("hacker")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
                .compact();

        mockMvc.perform(get("/api/v1/proyectos")
                        .header("Authorization", "Bearer " + fakeToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void whenXSSPayloadInProjectName_thenReturns400() throws Exception {
        String token = jwtUtil.generateToken("test-user");

        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("<script>alert('xss')</script>");
        request.setClientId(1);
        request.setType(ProjectType.DEVELOPMENT);
        request.setBillingType(ProjectBillingType.TIME_AND_MATERIAL);
        request.setStartDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/proyectos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenInvalidUUIDInLeaderId_thenReturns400() throws Exception {
        String token = jwtUtil.generateToken("test-user");

        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Proyecto Valido");
        request.setClientId(1);
        request.setType(ProjectType.DEVELOPMENT);
        request.setBillingType(ProjectBillingType.TIME_AND_MATERIAL);
        request.setStartDate(LocalDate.now().plusDays(1));
        // UUID inválido, simula SQL injection o error de input
        request.setLeaderId("12345678-1234-1234-1234-12345678901Z");

        mockMvc.perform(post("/api/v1/proyectos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenExceedsRateLimit_thenReturns429() throws Exception {
        // NOTA: Este test asume que el rate limit es 5 (por el @TestPropertySource).
        String token = jwtUtil.generateToken("test-user");

        // Realizamos 5 peticiones exitosas (o al menos no 429)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/proyectos")
                            .header("Authorization", "Bearer " + token)
                            .with(request -> { request.setRemoteAddr("10.0.0.1"); return request; }));
        }

        // La petición 6 debe fallar con 429 Too Many Requests
        mockMvc.perform(get("/api/v1/proyectos")
                        .header("Authorization", "Bearer " + token)
                        .with(request -> { request.setRemoteAddr("10.0.0.1"); return request; }))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("Too Many Requests"));
    }
}
