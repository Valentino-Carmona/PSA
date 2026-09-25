package com.psa.proyecto_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

/**
 * Entidad que representa un tag asociado a un proyecto.
 */
@Entity
@Table(name = "project_tags", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "tag_name"}),
       indexes = {
           @Index(name = "idx_project_tags_project_id", columnList = "project_id"),
           @Index(name = "idx_project_tags_name", columnList = "tag_name")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "project")
public class ProjectTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del tag es obligatorio")
    @Size(max = 50, message = "El nombre del tag no puede exceder los 50 caracteres")
    @Column(name = "tag_name", nullable = false)
    private String tagName;

    // Relaciones
    @NotNull(message = "El proyecto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Consturctor
    public ProjectTag(String tagName, Project project) {
        this.tagName = tagName;
        this.project = project;
    }
    
    // Métodos auxiliares
    
    /**
     * Verifica si este tag tiene el nombre especificado (ignorando mayúsculas/minúsculas).
     */
    public boolean hasTagName(String name) {
        if (name == null || this.tagName == null) {
            return false;
        }
        return this.tagName.trim().equalsIgnoreCase(name.trim());
    }

    /**
     * Actualiza el nombre del tag.
     */
    public void updateTagName(String newTagName) {
        if (newTagName == null || newTagName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre del tag no puede ser nulo o vacio");
        }
        this.tagName = newTagName;
    }
    
    /**
     * Verifica si este tag pertenece al proyecto especificado.
     */
    public boolean belongsToProject(Long projectId) {
        return this.project != null && 
               this.project.getId() != null && 
               this.project.getId().equals(projectId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectTag that)) return false;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}