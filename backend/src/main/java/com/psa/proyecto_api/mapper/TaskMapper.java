package com.psa.proyecto_api.mapper;

import org.springframework.stereotype.Component;

import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.Task;

@Component 
public class TaskMapper {
    
    public Task toEntity(CreateTaskRequest req, Project project) {
        Task task = new Task(
            req.getName(),
            project,
            req.getEstimatedHours()
        );

        if (req.getAssignedResourceId() != null) {
            task.addAssignedResource(req.getAssignedResourceId());
        }
        
        if (req.getTagNames() != null && !req.getTagNames().isEmpty()) {
            req.getTagNames().forEach(task::addTag);
        }

        if (req.getTicketId() != null) {
            task.setTicketId(req.getTicketId());
        }

        return task;
    }

    public void updateEntity(Task task, UpdateTaskRequest req) {
        task.updateDetails(
            req.getName(),
            req.getEstimatedHours(),
            req.getAssignedResourceId(),
            req.getTicketId()
        );
    }

    public TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .name(task.getName())
            .projectId(task.getProject().getId())
            .status(task.getStatus())
            .estimatedHours(task.getEstimatedHours())
            .assignedResourceId(task.getAssignedResourceId())
            .ticketId(task.getTicketId())
            .tagNames(task.getTagNames())
            .build();
    }

    public TaskSummaryResponse toSummary(Task task) {
        return TaskSummaryResponse.builder()
            .id(task.getId())
            .name(task.getName())
            .projectId(task.getProject().getId())
            .status(task.getStatus())
            .estimatedHours(task.getEstimatedHours())
            .assignedResourceId(task.getAssignedResourceId())
            .ticketId(task.getTicketId())
            .tagNames(task.getTagNames())
            .build();
    }
}
