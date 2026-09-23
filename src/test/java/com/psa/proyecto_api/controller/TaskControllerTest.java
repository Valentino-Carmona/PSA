package com.psa.proyecto_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;
import com.psa.proyecto_api.model.enums.TaskStatus;
import com.psa.proyecto_api.service.TaskService;
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

import java.util.List;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        taskResponse = TaskResponse.builder()
                .id(1L)
                .name("Tarea Alpha")
                .projectId(10L)
                .ticketId(1001)
                .status(TaskStatus.TO_DO)
                .estimatedHours(8)
                .assignedResourceId("123e4567-e89b-12d3-a456-426614174001")
                .tagNames(List.of("Frontend"))
                .build();
    }

    @Test
    void createTask_ValidRequest_ReturnsCreatedAndTaskResponse() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("Tarea Alpha");
        request.setEstimatedHours(8);
        request.setAssignedResourceId("123e4567-e89b-12d3-a456-426614174001");
        request.setTicketId(1001);

        when(taskService.createTask(eq(10L), any(CreateTaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/v1/proyectos/10/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tarea Alpha"))
                .andExpect(jsonPath("$.projectId").value(10L))
                .andExpect(jsonPath("$.estimatedHours").value(8));
    }

    @Test
    void createTask_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("Tarea Alpha");
        request.setEstimatedHours(-5); // Horas negativas es invalido

        mockMvc.perform(post("/api/v1/proyectos/10/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_ValidRequest_ReturnsOkAndTaskResponse() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setName("Tarea Modificada");
        request.setEstimatedHours(10);
        request.setAssignedResourceId("123e4567-e89b-12d3-a456-426614174002");
        request.setTicketId(1002);

        TaskResponse updatedResponse = TaskResponse.builder()
                .id(1L)
                .name("Tarea Modificada")
                .projectId(10L)
                .ticketId(1002)
                .status(TaskStatus.TO_DO)
                .estimatedHours(10)
                .assignedResourceId("123e4567-e89b-12d3-a456-426614174002")
                .tagNames(Collections.emptyList())
                .build();
        
        when(taskService.updateTask(eq(1L), any(UpdateTaskRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/tareas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tarea Modificada"))
                .andExpect(jsonPath("$.estimatedHours").value(10));
    }

    @Test
    void activateTask_ReturnsOk() throws Exception {
        TaskResponse activeResponse = TaskResponse.builder()
                .id(1L)
                .name("Tarea Alpha")
                .projectId(10L)
                .ticketId(1001)
                .status(TaskStatus.IN_PROGRESS)
                .estimatedHours(8)
                .assignedResourceId("123e4567-e89b-12d3-a456-426614174001")
                .tagNames(Collections.emptyList())
                .build();
                
        when(taskService.activateTask(1L)).thenReturn(activeResponse);

        mockMvc.perform(put("/api/v1/tareas/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deactivateTask_ReturnsOk() throws Exception {
        when(taskService.deactivateTask(1L)).thenReturn(taskResponse);

        mockMvc.perform(put("/api/v1/tareas/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TO_DO"));
    }

    @Test
    void getProjectTasks_ReturnsOk() throws Exception {
        TaskSummaryResponse summary = TaskSummaryResponse.builder()
                .id(1L)
                .name("Tarea Alpha")
                .projectId(10L)
                .ticketId(1001)
                .status(TaskStatus.TO_DO)
                .estimatedHours(8)
                .assignedResourceId("123e4567-e89b-12d3-a456-426614174001")
                .tagNames(List.of("Frontend"))
                .build();
                
        when(taskService.getProjectTasksFiltered(eq(10L), isNull(), isNull(), isNull(), isNull())).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/proyectos/10/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Tarea Alpha"));
    }

    @Test
    void getTasksByTicketId_ReturnsOk() throws Exception {
        TaskSummaryResponse summary = TaskSummaryResponse.builder()
                .id(1L)
                .name("Tarea Alpha")
                .projectId(10L)
                .ticketId(1001)
                .status(TaskStatus.TO_DO)
                .estimatedHours(8)
                .assignedResourceId("123e4567-e89b-12d3-a456-426614174001")
                .tagNames(List.of("Frontend"))
                .build();
                
        when(taskService.getTasksByTicketId(1001)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/tareas?ticketId=1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTaskById_ReturnsOk() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);

        mockMvc.perform(get("/api/v1/tareas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tarea Alpha"));
    }

    @Test
    void deleteTask_ReturnsNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/v1/tareas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void addTagToTask_ReturnsOk() throws Exception {
        when(taskService.addTagToTask(1L, "Backend")).thenReturn(taskResponse);

        mockMvc.perform(post("/api/v1/tareas/1/tags")
                .contentType(MediaType.TEXT_PLAIN) 
                .content("Backend"))
                .andExpect(status().isOk());
    }

    @Test
    void removeTagFromTask_ReturnsOk() throws Exception {
        when(taskService.removeTagFromTask(1L, "Frontend")).thenReturn(taskResponse);

        mockMvc.perform(delete("/api/v1/tareas/1/tags?tagName=Frontend"))
                .andExpect(status().isOk());
    }

    @Test
    void updateTaskTag_ReturnsOk() throws Exception {
        when(taskService.updateTaskTag(1L, "Frontend", "UI")).thenReturn(taskResponse);

        mockMvc.perform(put("/api/v1/tareas/1/tags?oldTagName=Frontend")
                .contentType(MediaType.TEXT_PLAIN)
                .content("UI"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaskTags_ReturnsOk() throws Exception {
        when(taskService.getTaskTags(1L)).thenReturn(List.of("Frontend"));

        mockMvc.perform(get("/api/v1/tareas/1/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("Frontend"));
    }
}
