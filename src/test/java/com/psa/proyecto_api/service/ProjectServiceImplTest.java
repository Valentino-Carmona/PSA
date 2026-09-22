package com.psa.proyecto_api.service;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.ProjectFilterRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.exception.ProjectNotFoundException;
import com.psa.proyecto_api.mapper.ProjectMapper;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.repository.ProjectRepository;
import com.psa.proyecto_api.service.impl.ProjectServiceImpl;
import com.psa.proyecto_api.specification.ProjectSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectSpecifications projectSpecifications;
    @Mock private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStatus(ProjectStatus.INITIATED);
        project.setType(ProjectType.DEVELOPMENT);
        project.setBillingType(ProjectBillingType.FIXED_PRICE);
        project.setStartDate(LocalDate.now());

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .name("Test Project")
                .build();
    }

    @Test
    void createProject_ReturnsProjectResponse() {
        CreateProjectRequest request = new CreateProjectRequest();
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.createProject(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_WhenExists_ReturnsUpdatedResponse() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.updateProject(1L, request);

        assertNotNull(result);
        verify(projectMapper).updateEntity(project, request);
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_WhenNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(99L, new UpdateProjectRequest()));
    }

    @Test
    void getProjectById_WhenExists_ReturnsResponse() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getProjectById_WhenNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(99L));
    }

    @Test
    void getProjects_ReturnsFilteredList() {
        ProjectFilterRequest filterRequest = new ProjectFilterRequest();
        ProjectSummaryResponse summary = ProjectSummaryResponse.builder().id(1L).name("Test").build();
        when(projectSpecifications.withFilters(filterRequest)).thenReturn(Specification.where(null));
        when(projectRepository.findAll(any(Specification.class))).thenReturn(List.of(project));
        when(projectMapper.toSummaryList(anyList())).thenReturn(List.of(summary));

        List<ProjectSummaryResponse> results = projectService.getProjects(filterRequest);

        assertEquals(1, results.size());
    }

    @Test
    void searchProjects_ReturnsMatchingProjects() {
        ProjectSummaryResponse summary = ProjectSummaryResponse.builder().id(1L).name("Test").build();
        when(projectRepository.findByNameContainingIgnoreCase("Test")).thenReturn(List.of(project));
        when(projectMapper.toSummaryList(anyList())).thenReturn(List.of(summary));

        List<ProjectSummaryResponse> results = projectService.searchProjects("Test");

        assertEquals(1, results.size());
    }

    @Test
    void deleteProject_WhenExists_DeletesSuccessfully() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        projectService.deleteProject(1L);

        verify(projectRepository).deleteById(1L);
    }

    @Test
    void deleteProject_WhenNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(99L));
        verify(projectRepository, never()).deleteById(any());
    }

    @Test
    void addTagToProject_WhenExists_ReturnsUpdatedProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.addTagToProject(1L, "backend");

        assertNotNull(result);
        verify(projectRepository).save(project);
    }

    @Test
    void addTagToProject_WhenNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.addTagToProject(99L, "backend"));
    }

    @Test
    void removeTagFromProject_WhenExists_ReturnsUpdatedProject() {
        project.addTag("backend");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.removeTagFromProject(1L, "backend");

        assertNotNull(result);
    }

    @Test
    void removeTagFromProject_WhenNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.removeTagFromProject(99L, "backend"));
    }

    @Test
    void updateProjectTag_WhenExists_ReturnsUpdatedProject() {
        project.addTag("oldTag");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.updateProjectTag(1L, "oldTag", "newTag");

        assertNotNull(result);
    }

    @Test
    void updateProjectTag_WhenNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProjectTag(99L, "old", "new"));
    }

    @Test
    void getProjectTags_WhenExists_ReturnsTags() {
        project.addTag("backend");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        List<String> tags = projectService.getProjectTags(1L);

        assertNotNull(tags);
        assertTrue(tags.contains("backend"));
    }

    @Test
    void getProjectTags_WhenNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectTags(99L));
    }

    @Test
    void getProjectsFiltered_WithNullParams_ReturnsAll() {
        ProjectSummaryResponse summary = ProjectSummaryResponse.builder().id(1L).name("Test").build();
        when(projectRepository.findByProgressiveFilters(null, null, null, null)).thenReturn(List.of(project));
        when(projectMapper.toSummary(project)).thenReturn(summary);

        List<ProjectSummaryResponse> results = projectService.getProjectsFiltered(null, null, null, null);

        assertEquals(1, results.size());
    }
}
