package com.psa.proyecto_api.dto.response;

import com.psa.proyecto_api.model.enums.TaskStatus;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TaskResponse {
    private Long id;
    private String name;
    private Long projectId;
    private Integer ticketId;
    private TaskStatus status;
    private Integer estimatedHours;
    private String assignedResourceId;
    private List<String> tagNames;
}
