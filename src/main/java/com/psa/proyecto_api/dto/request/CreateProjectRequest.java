package com.psa.proyecto_api.dto.request;

import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,():/]+$", message = "El nombre contiene caracteres inválidos")
    private String name;
        
    @NotNull(message = "El tipo de proyecto es obligatorio")
    private ProjectType type;
    
    @NotNull(message = "El tipo de facturación es obligatorio")
    private ProjectBillingType billingType;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
    private LocalDate startDate;
    
    @NotNull(message = "El cliente es obligatorio")
    private Integer clientId;
    
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate endDate;
    
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "El id del lider debe ser un UUID válido")
    private String leaderId;
    
    private List<@NotBlank(message = "La etiqueta no puede estar vacía") @Size(max = 50, message = "La etiqueta es muy larga") @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,():/]+$", message = "La etiqueta contiene caracteres inválidos") String> tagNames;
}
