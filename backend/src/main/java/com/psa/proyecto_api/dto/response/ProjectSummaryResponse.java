package com.psa.proyecto_api.dto.response;

import com.psa.proyecto_api.model.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class ProjectSummaryResponse {
    private Long id;
    private String name;
    private Integer clientId;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> tagNames;
}

