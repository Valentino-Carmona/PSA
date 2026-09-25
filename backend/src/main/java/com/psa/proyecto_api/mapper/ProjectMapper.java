package com.psa.proyecto_api.mapper;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.model.Project;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public Project toEntity(CreateProjectRequest request) {
        Project project = new Project(
                request.getName(),
                request.getClientId(),
                request.getType(),
                request.getBillingType(),
                request.getStartDate()
        );

        if (request.getEndDate() != null) {
            project.addEndDate(request.getEndDate());
        }

        if (request.getLeaderId() != null) {
            project.addLeader(request.getLeaderId());
        }

        if (request.getTagNames() != null) {
            request.getTagNames().forEach(project::addTag);
        }
        return project;
    }

    public void updateEntity(Project project, UpdateProjectRequest request) {
        project.updateDetails(
                request.getName(),
                request.getClientId(),
                request.getLeaderId()
        );
        project.updateTypes(
                request.getType(),
                request.getBillingType()
        );
        project.updateDates(
                request.getStartDate(),
                request.getEndDate()
        );
    }

    public ProjectResponse toResponse(Project project) {
        TaskMapper taskMapper = new TaskMapper();
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .clientId(project.getClientId())
                .leaderId(project.getLeaderId())
                .type(project.getType())
                .billingType(project.getBillingType())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .estimatedHours(project.getEstimatedHours())
                
                .tagNames(project.getTagNames())
                .tasks(project.getTasks().stream()
                        .map(taskMapper::toResponse)
                        .toList())
                
                .build();
    }

    public ProjectSummaryResponse toSummary(Project project) {
        return ProjectSummaryResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .clientId(project.getClientId())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .tagNames(project.getTagNames())
                .build();
    }

    public List<ProjectSummaryResponse> toSummaryList(List<Project> projects) {
        return projects.stream()
                .map(this::toSummary)
                .toList();
    }
}
