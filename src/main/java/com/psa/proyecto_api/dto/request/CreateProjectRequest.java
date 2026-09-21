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
    
    @Size(min = 36, max = 36, message = "El id del lider debe tener 36 caracteres")
    private String leaderId;
    
    private List<String> tagNames;
}
