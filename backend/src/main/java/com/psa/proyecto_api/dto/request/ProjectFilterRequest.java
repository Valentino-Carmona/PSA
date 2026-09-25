package com.psa.proyecto_api.dto.request;

import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterRequest {

    @Schema(example = "IN_PROGRESS", description = "Estado del proyecto", required = false)
    @Valid
    private ProjectStatus status;

    @Schema(example = "DEVELOPMENT", description = "Tipo de proyecto", required = false)
    @Valid
    private ProjectType type;

    @Schema(example = "name_tag", description = "Tag del proyecto", required = false)
    @jakarta.validation.constraints.Size(max = 50, message = "El tag no puede exceder los 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "El tag solo puede contener letras, números, guiones y guiones bajos")
    private String tag;
}
