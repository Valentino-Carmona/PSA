package com.psa.proyecto_api.security;

import com.psa.proyecto_api.controller.AuthController;
import com.psa.proyecto_api.controller.ProjectController;
import com.psa.proyecto_api.service.ProjectService;
import com.psa.proyecto_api.service.ExternalApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ProjectController.class, AuthController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class})
public class SecurityWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ExternalApiService externalApiService;

    @Test
    void whenAccessProtectedEndpointWithoutToken_thenReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/proyectos"))
               .andExpect(status().isForbidden());
    }

    @Test
    void whenLoginWithValidCredentials_thenReturns200AndToken() throws Exception {
        String loginPayload = "{\"username\": \"admin\", \"password\": \"secret\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
               .andExpect(status().isOk());
    }
}
