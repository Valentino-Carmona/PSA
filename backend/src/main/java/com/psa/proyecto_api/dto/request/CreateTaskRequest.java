package com.psa.proyecto_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateTaskRequest {
    
    @NotBlank(message = "El nombre de la tarea es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,():/]+$", message = "El nombre contiene caracteres inválidos")
    private String name;
    
    @NotNull(message = "Las horas estimadas son obligatorias")
    @Min(value = 1, message = "Las horas estimadas no pueden ser negativas o cero")
    private Integer estimatedHours;
    
    private Integer ticketId;
    
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "El id del recurso asignado debe ser un UUID válido")
    private String assignedResourceId;

    private List<@NotBlank(message = "La etiqueta no puede estar vacía") @Size(max = 50, message = "La etiqueta es muy larga") @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,():/]+$", message = "La etiqueta contiene caracteres inválidos") String> tagNames;
}
