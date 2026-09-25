package com.psa.proyecto_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.psa.proyecto_api.security.JwtUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProjectController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        projectResponse = ProjectResponse.builder()
                .id(10L)
                .name("Proyecto Omega")
                .clientId(1)
                .leaderId("123e4567-e89b-12d3-a456-426614174001")
                .type(ProjectType.DEVELOPMENT)
                .billingType(ProjectBillingType.FIXED_PRICE)
                .status(ProjectStatus.INITIATED)
                .startDate(LocalDate.now())
                .estimatedHours(100)
                .tagNames(List.of("Backend"))
                .tasks(Collections.emptyList())
                .build();
    }

    @Test
    void createProject_ValidRequest_ReturnsCreated() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Proyecto Omega");
        request.setClientId(1);
        request.setLeaderId("123e4567-e89b-12d3-a456-426614174001");
        request.setType(ProjectType.DEVELOPMENT);
        request.setBillingType(ProjectBillingType.FIXED_PRICE);
        request.setStartDate(LocalDate.now());

        when(projectService.createProject(any(CreateProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post("/api/v1/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Proyecto Omega"));
    }

    @Test
    void createProject_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(""); // Invalid name

        mockMvc.perform(post("/api/v1/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProject_ValidRequest_ReturnsOk() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Proyecto Actualizado");

        ProjectResponse updatedResponse = ProjectResponse.builder()
                .id(10L)
                .name("Proyecto Actualizado")
                .clientId(1)
                .type(ProjectType.DEVELOPMENT)
                .billingType(ProjectBillingType.FIXED_PRICE)
                .status(ProjectStatus.INITIATED)
                .startDate(LocalDate.now())
                .estimatedHours(100)
                .tagNames(List.of("Backend"))
                .tasks(Collections.emptyList())
                .build();

        when(projectService.updateProject(eq(10L), any(UpdateProjectRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/proyectos/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Proyecto Actualizado"));
    }

    @Test
    void deleteProject_ReturnsNoContent() throws Exception {
        doNothing().when(projectService).deleteProject(10L);

        mockMvc.perform(delete("/api/v1/proyectos/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProjects_ReturnsOk() throws Exception {
        ProjectSummaryResponse summary = ProjectSummaryResponse.builder()
                .id(10L)
                .name("Proyecto Omega")
                .clientId(1)
                .status(ProjectStatus.INITIATED)
                .startDate(LocalDate.now())
                .tagNames(List.of("Backend"))
                .build();
                
        when(projectService.getProjectsFiltered(isNull(), isNull(), isNull(), isNull())).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/proyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Proyecto Omega"));
    }

    @Test
    void getProjectById_ReturnsOk() throws Exception {
        when(projectService.getProjectById(10L)).thenReturn(projectResponse);

        mockMvc.perform(get("/api/v1/proyectos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Proyecto Omega"));
    }

    @Test
    void addTagToProject_ReturnsOk() throws Exception {
        when(projectService.addTagToProject(10L, "Backend")).thenReturn(projectResponse);

        mockMvc.perform(post("/api/v1/proyectos/10/tags")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Backend"))
                .andExpect(status().isOk());
    }

    @Test
    void removeTagFromProject_ReturnsOk() throws Exception {
        when(projectService.removeTagFromProject(10L, "Backend")).thenReturn(projectResponse);

        mockMvc.perform(delete("/api/v1/proyectos/10/tags?tagName=Backend"))
                .andExpect(status().isOk());
    }

    @Test
    void updateProjectTag_ReturnsOk() throws Exception {
        when(projectService.updateProjectTag(10L, "Backend", "API")).thenReturn(projectResponse);

        mockMvc.perform(put("/api/v1/proyectos/10/tags?oldTagName=Backend")
                .contentType(MediaType.TEXT_PLAIN)
                .content("API"))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectTags_ReturnsOk() throws Exception {
        when(projectService.getProjectTags(10L)).thenReturn(List.of("Backend"));

        mockMvc.perform(get("/api/v1/proyectos/10/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("Backend"));
    }
}
