package com.psa.proyecto_api.service;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.ProjectFilterRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(CreateProjectRequest request);
    ProjectResponse updateProject(Long id, UpdateProjectRequest request);
    List<ProjectSummaryResponse> getProjects(ProjectFilterRequest filterRequest);
    List<ProjectSummaryResponse> searchProjects(String name);
    ProjectResponse getProjectById(Long id);
    List<ProjectSummaryResponse> getProjectsFiltered(String nombre, String tipo, String estado, String tag);
    void deleteProject(Long id);

    ProjectResponse addTagToProject(Long id, String tag);
    ProjectResponse removeTagFromProject(Long id, String tag);
    ProjectResponse updateProjectTag(Long id, String oldTag, String newTag);
    List<String> getProjectTags(Long id);
}
