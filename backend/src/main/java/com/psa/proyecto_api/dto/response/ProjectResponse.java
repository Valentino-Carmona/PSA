package com.psa.proyecto_api.dto.response;

import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class ProjectResponse {
    private Long id;
    private String name;
    private Integer clientId;
    private String leaderId;
    private ProjectType type;
    private ProjectBillingType billingType;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer estimatedHours;
    
    private List<String> tagNames;
    private List<TaskResponse> tasks;
}
