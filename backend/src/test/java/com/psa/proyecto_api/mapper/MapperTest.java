package com.psa.proyecto_api.mapper;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.Tag;
import com.psa.proyecto_api.model.Task;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.model.enums.TaskStatus;
import com.psa.proyecto_api.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapperTest {

    @Mock
    private TagRepository tagRepository;

    private TaskMapper taskMapper;
    private ProjectMapper projectMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper(tagRepository);
        projectMapper = new ProjectMapper(tagRepository, taskMapper);
    }

    @Test
    void testProjectMapper_ToEntity_Full() {
        when(tagRepository.findByName("java")).thenReturn(Optional.of(new Tag("java")));
        when(tagRepository.findByName("spring")).thenReturn(Optional.empty());

        CreateProjectRequest req = new CreateProjectRequest();
        req.setName("Test Project");
        req.setClientId(1);
        req.setType(ProjectType.DEVELOPMENT);
        req.setBillingType(ProjectBillingType.FIXED_PRICE);
        req.setStartDate(LocalDate.of(2025, 1, 1));
        req.setEndDate(LocalDate.of(2025, 12, 31));
        req.setLeaderId("10");
        req.setTagNames(Arrays.asList("Java", "Spring"));

        Project p = projectMapper.toEntity(req);

        assertEquals("Test Project", p.getName());
        assertEquals(1, p.getClientId());
        assertEquals(ProjectType.DEVELOPMENT, p.getType());
        assertEquals(ProjectBillingType.FIXED_PRICE, p.getBillingType());
        assertEquals(LocalDate.of(2025, 1, 1), p.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), p.getEndDate());
        assertEquals("10", p.getLeaderId());
        assertTrue(p.getTagNames().contains("java"));
        assertTrue(p.getTagNames().contains("spring"));
    }

    @Test
    void testProjectMapper_UpdateEntity() {
        Project project = new Project("Old", 2, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        UpdateProjectRequest req = new UpdateProjectRequest();
        req.setName("New Name");
        req.setClientId(3);
        req.setLeaderId("5");
        req.setType(ProjectType.IMPLEMENTATION);
        req.setBillingType(ProjectBillingType.TIME_AND_MATERIAL);
        req.setStartDate(LocalDate.of(2026, 1, 1));
        req.setEndDate(LocalDate.of(2026, 12, 31));

        projectMapper.updateEntity(project, req);

        assertEquals("New Name", project.getName());
        assertEquals(2, project.getClientId()); // No se actualiza por diseño
        assertEquals("5", project.getLeaderId());
        assertEquals(ProjectType.IMPLEMENTATION, project.getType());
        assertEquals(ProjectBillingType.TIME_AND_MATERIAL, project.getBillingType());
        assertEquals(LocalDate.of(2026, 1, 1), project.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 31), project.getEndDate());
    }

    @Test
    void testProjectMapper_ToResponse() {
        Project p = new Project("P", 1, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        p.addTag(new Tag("test"));
        
        ProjectResponse res = projectMapper.toResponse(p);

        assertEquals("P", res.getName());
        assertEquals(1, res.getClientId());
        assertEquals(ProjectType.DEVELOPMENT, res.getType());
        assertEquals(ProjectBillingType.FIXED_PRICE, res.getBillingType());
        assertEquals(ProjectStatus.INITIATED, res.getStatus());
        assertTrue(res.getTagNames().contains("test"));
    }

    @Test
    void testProjectMapper_ToSummaryAndList() {
        Project p = new Project("P", 1, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        p.addTag(new Tag("test"));
        
        ProjectSummaryResponse res = projectMapper.toSummary(p);
        assertEquals("P", res.getName());
        assertTrue(res.getTagNames().contains("test"));

        List<ProjectSummaryResponse> list = projectMapper.toSummaryList(Collections.singletonList(p));
        assertEquals(1, list.size());
        assertEquals("P", list.get(0).getName());
    }

    @Test
    void testTaskMapper_ToEntity_Full() {
        when(tagRepository.findByName("bug")).thenReturn(Optional.of(new Tag("bug")));
        when(tagRepository.findByName("ui")).thenReturn(Optional.empty());

        Project p = new Project("P", 1, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        CreateTaskRequest req = new CreateTaskRequest();
        req.setName("Task");
        req.setEstimatedHours(10);
        req.setAssignedResourceId("100");
        req.setTicketId(200);
        req.setTagNames(Arrays.asList("Bug", "UI"));

        Task t = taskMapper.toEntity(req, p);

        assertEquals("Task", t.getName());
        assertEquals(10, t.getEstimatedHours());
        assertEquals("100", t.getAssignedResourceId());
        assertEquals(200, t.getTicketId());
        assertTrue(t.getTagNames().contains("bug"));
        assertTrue(t.getTagNames().contains("ui"));
    }

    @Test
    void testTaskMapper_UpdateEntity() {
        Project p = new Project("P", 1, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        Task task = new Task("Old", p, 5);
        
        UpdateTaskRequest req = new UpdateTaskRequest();
        req.setName("New");
        req.setEstimatedHours(20);
        req.setAssignedResourceId("50");
        req.setTicketId(500);

        taskMapper.updateEntity(task, req);

        assertEquals("New", task.getName());
        assertEquals(20, task.getEstimatedHours());
        assertEquals("50", task.getAssignedResourceId());
        assertEquals(500, task.getTicketId());
    }

    @Test
    void testTaskMapper_ToResponseAndSummary() {
        Project p = new Project("P", 1, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        Task task = new Task("T", p, 5);
        task.addAssignedResource("10");
        task.setTicketId(20);
        task.addTag(new Tag("tag1"));

        TaskResponse res = taskMapper.toResponse(task);
        assertEquals("T", res.getName());
        assertEquals(TaskStatus.TO_DO, res.getStatus());
        assertEquals("10", res.getAssignedResourceId());
        assertEquals(20, res.getTicketId());
        assertTrue(res.getTagNames().contains("tag1"));

        TaskSummaryResponse summary = taskMapper.toSummary(task);
        assertEquals("T", summary.getName());
        assertEquals("10", summary.getAssignedResourceId());
    }
}
