package com.psa.proyecto_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class UpdateTaskRequest {
    
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    @Min(value = 1, message = "Las horas estimadas no pueden ser negativas")
    @Schema(example = "1", description = "Horas estimadas para completar la tarea")
    private Integer estimatedHours;

    private Integer ticketId;
    
    @Size(min = 36, max = 36, message = "El id del recurso debe tener 36 caracteres")
    private String assignedResourceId;
}
