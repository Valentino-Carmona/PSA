package com.psa.proyecto_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

/**
 * Entidad que representa un tag asociado a una tarea.
 */
@Entity
@Table(name = "task_tags", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "tag_name"}),
       indexes = {
           @Index(name = "idx_task_tags_task_id", columnList = "task_id"),
           @Index(name = "idx_task_tags_name", columnList = "tag_name")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "task")
public class TaskTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del tag es obligatorio")
    @Size(max = 50, message = "El nombre del tag no puede exceder los 50 caracteres")
    @Column(name = "tag_name", nullable = false)
    private String tagName;

    // Relaciones
    @NotNull(message = "La tarea es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // Consturctor
    public TaskTag(String tagName, Task task) {
        this.tagName = tagName;
        this.task = task;
    }
    
    // Métodos auxiliares
    
    /**
     * Verifica si este tag tiene el nombre especificado (ignorando mayúsculas/minúsculas).
     */
    public boolean hasTagName(String name) {
        if (name == null || this.tagName == null) {
            return false;
        }
        return this.tagName.equalsIgnoreCase(name.trim());
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
     * Verifica si este tag pertenece a la tarea especificada.
     */
    public boolean belongsToTask(Long taskId) {
        return this.task != null && this.task.hasId(taskId);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskTag taskTag)) return false;
        return Objects.equals(id, taskTag.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}