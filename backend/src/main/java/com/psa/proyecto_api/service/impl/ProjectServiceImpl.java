package com.psa.proyecto_api.service.impl;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.ProjectFilterRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.exception.ProjectNotFoundException;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.repository.ProjectRepository;
import com.psa.proyecto_api.service.ProjectService;
import com.psa.proyecto_api.specification.ProjectSpecifications;
import com.psa.proyecto_api.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectSpecifications projectSpecifications;
    private final ProjectMapper projectMapper;

    // Project Methods

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {
        Project project = projectMapper.toEntity(request);
        project = projectRepository.save(project);
        
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        projectMapper.updateEntity(project, request);
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getProjects(ProjectFilterRequest filterRequest) {
        
        Specification<Project> specification = projectSpecifications.withFilters(filterRequest);
        List<Project> projects = projectRepository.findAll(specification);
        return projectMapper.toSummaryList(projects);
    }

    @Override
    public List<ProjectSummaryResponse> searchProjects(String name) {
        List<Project> projects = projectRepository.findByNameContainingIgnoreCase(name);
        return projectMapper.toSummaryList(projects);
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getProjectsFiltered(String nombre, String tipo, String estado, String tag) {
        ProjectType projectType = ProjectType.fromString(tipo);
        ProjectStatus projectStatus = ProjectStatus.fromString(estado);

        List<Project> projects = projectRepository.findByProgressiveFilters(nombre, projectType, projectStatus, tag);
        return projects.stream()
                .map(projectMapper::toSummary)
                .toList();
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) throw new ProjectNotFoundException(id);
        projectRepository.deleteById(id);
    }

    // ProjectTag Methods

    @Override
    public ProjectResponse addTagToProject(Long id, String tag) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        project.addTag(tag);
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse removeTagFromProject(Long id, String tag) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        project.removeTag(tag);
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse updateProjectTag(Long id, String oldTag, String newTag) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        project.updateProjectTag(oldTag, newTag);
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public List<String> getProjectTags(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return project.getTagNames();
    }
}
