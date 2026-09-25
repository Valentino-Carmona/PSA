package com.psa.proyecto_api.model;

import com.psa.proyecto_api.model.enums.TaskStatus;
import com.psa.proyecto_api.exception.OperationNotAllowedException;
import com.psa.proyecto_api.exception.ResourceConflictException;
import com.psa.proyecto_api.exception.InvalidProjectStatusException;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una tarea dentro de un proyecto.
 * 
 * Una tarea pertenece a un proyecto y tiene tags asociados.
 */
@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_tasks_project_id", columnList = "project_id"),
    @Index(name = "idx_tasks_status", columnList = "status"),
    @Index(name = "idx_tasks_assigned_resource", columnList = "assigned_resource_id"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"project", "taskTags"})
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de la tarea es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TO_DO;
    
    @NotNull(message = "Las horas estimadas son obligatorias")
    @Min(value = 1, message = "Las horas estimadas no pueden ser negativas o cero")
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    // Relaciones externas
    @Column(name = "assigned_resource_id")
    @Size(min = 36, max = 36, message = "El id del recurso debe tener 36 caracteres")
    private String assignedResourceId;

    @Column(name = "ticket_id")
    private Integer ticketId;
    
    // Relaciones internas
    @NotNull(message = "El proyecto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TaskTag> taskTags = new ArrayList<>();
    
    // Constructor
    public Task(String name, Project project, Integer estimatedHours) {
        this.name = name;
        this.project = project;
        this.estimatedHours = estimatedHours;
        this.taskTags = new ArrayList<>();
        this.status = TaskStatus.TO_DO;

        // Establecer la relacion bidireccional con el proyecto
        this.project.addTask(this);
    }

    public void addAssignedResource(String assignedResourceId) {
        if (assignedResourceId == null || assignedResourceId.trim().isEmpty()) {
            throw new OperationNotAllowedException("El ID del recurso no puede ser nulo o vacio.");
        }
        this.assignedResourceId = assignedResourceId;
    }

    // Metodos de gestion de proyecto
    /**
     * Establece el proyecto al que pertenece la tarea
     */
    public void setProject(Project project) {
        if (project == null) {
            throw new OperationNotAllowedException("El proyecto no puede ser nulo");
        }
        this.project = project;
    }

    /**
     * Eliminarme del proyecto al que pertenezco.
     */
    public void removeFromProject() {
        this.project.removeTask(this);
    }

    // Metodos de gestion de horas

    // Métodos de gestión de tags
    
    /**
     * Agrega un tag a la tarea evitando duplicados.
     */
    public void addTag(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new OperationNotAllowedException("El nombre del tag no puede ser nulo o vacío");
        }
        String normalizedTagName = tagName.trim();
        
        // Verificar si el tag ya existe
        boolean tagExists = taskTags.stream()
            .anyMatch(taskTag -> taskTag.hasTagName(normalizedTagName));
            
        if (tagExists) {
            throw new ResourceConflictException("El tag '" + tagName + "' ya existe en esta tarea");
        }
        
        TaskTag newTag = TaskTag.builder()
            .tagName(normalizedTagName)
            .task(this)
            .build();
        
        taskTags.add(newTag);
    }

    /**
     * Actualiza los detalles de una tarea.
     */
    public void updateDetails(String name, Integer estimatedHours, String assignedResourceId, Integer ticketId) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        
        }
        if (estimatedHours != null && estimatedHours > 0) {
            this.estimatedHours = estimatedHours;
            this.project.updateProjectStatusAndHours();

        }
        if (assignedResourceId != null && !assignedResourceId.trim().isEmpty()) {
            this.assignedResourceId = assignedResourceId;

        } 
        if (ticketId != null) {
            this.ticketId = ticketId;
        }
    }

    /**
     * Actualiza un tag de la tarea.
     */
    public void updateTaskTag(String oldTag, String newTag) {
        if (oldTag == null || oldTag.isEmpty()) {
            throw new OperationNotAllowedException("El nombre del tag actual no puede ser nulo o vacío");
        }
        
        if (newTag == null || newTag.isEmpty()) {
            throw new OperationNotAllowedException("El nombre del nuevo tag no puede ser nulo o vacío");
        }

        // Verificar si el nuevo tag ya existe (evitar duplicados)
        boolean newTagExists = taskTags.stream()
            .anyMatch(taskTag -> taskTag.hasTagName(newTag));
            
        if (newTagExists) {
            throw new ResourceConflictException("El tag '" + newTag + "' ya existe en esta tarea");
        }

        for (TaskTag taskTag : this.taskTags) {
            if (taskTag.hasTagName(oldTag)) {
                taskTag.updateTagName(newTag);
                return;
            }
        }
        
        throw new OperationNotAllowedException("El tag '" + oldTag + "' no existe en esta tarea");
    }

    /**
     * Remueve un tag de la tarea.
     */
    public void removeTag(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new OperationNotAllowedException("El nombre del tag no puede ser nulo o vacío");
        }
        String normalizedTagName = tagName.trim();
        
        boolean removed = taskTags.removeIf(taskTag -> taskTag.hasTagName(normalizedTagName));
        
        if (!removed) {
            throw new OperationNotAllowedException("El tag '" + tagName + "' no existe en esta tarea");
        }
    }
    
    // Métodos de consulta de estado
    
    /**
     * Verifica si la tarea está pendiente por hacer.
     */
    public boolean isToDo() {
        return status == TaskStatus.TO_DO;
    }

    /**
     * Verifica si la tarea está activa (no completada ni cancelada).
     */
    public boolean isActive() {
        return status == TaskStatus.IN_PROGRESS;
    }
    
    /**
     * Verifica si la tarea ha finalizado.
     */
    public boolean isFinished() {
        return status == TaskStatus.DONE;
    }
    
    /**
     * Verifica si la tarea está asignada a un recurso.
     */
    public boolean isAssigned() {
        return assignedResourceId != null;
    }

    /**
     * Verifica si el id de la tarea es igual al id proporcionado.
     */
    public boolean hasId(Long taskId) {
        return this.id != null && this.id.equals(taskId);
    }
    
    // Métodos auxiliares para cálculos
    
    /**
     * Obtiene las horas estimadas o cero si es null.
     */
    public int getEstimatedHoursOrZero() {
        return estimatedHours != null ? estimatedHours : 0;
    }
    
    /**
     * Retorna 1 si la tarea está finalizada, 0 en caso contrario.
     * Útil para operaciones de suma en streams.
     */
    public long isFinishedAsLong() {
        return isFinished() ? 1L : 0L;
    }
    
    /**
     * Retorna 1 si la tarea está activa, 0 en caso contrario.
     * Útil para operaciones de suma en streams.
     */
    public long isActiveAsLong() {
        return isActive() ? 1L : 0L;
    }
    
    /**
     * Verifica si la tarea tiene un tag específico.
     */
    public boolean hasTag(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            return false;
        }
        
        String normalizedTagName = tagName.trim();
        return taskTags.stream()
            .anyMatch(taskTag -> taskTag.hasTagName(normalizedTagName));
    }
    
    /**
     * Obtiene todos los nombres de tags de la tarea.
     */
    public List<String> getTagNames() {
        return taskTags.stream()
            .map(TaskTag::getTagName)
            .sorted()
            .toList();
    }
    
    // Métodos de transición de estado
    
    /**
     * Inicia la tarea cambiando su estado a IN_PROGRESS.
     * @return true si la tarea fue iniciada exitosamente, false si la tarea no está en estado TO_DO
     */
    public void start() {
        if (status != TaskStatus.TO_DO) {
            throw new InvalidProjectStatusException("No se puede iniciar una tarea que no esté en estado TO_DO. Estado actual: " + status);
        }
        
        this.status = TaskStatus.IN_PROGRESS;
        this.project.statusSwitch();
    }
    
    /**
     * Completa la tarea cambiando su estado a DONE.
     */
    public void complete() {
        if (status != TaskStatus.IN_PROGRESS) {
            throw new InvalidProjectStatusException("No se puede completar una tarea que no esté en estado IN_PROGRESS. Estado actual: " + status);
        }
        
        this.status = TaskStatus.DONE;
        this.project.statusSwitch();
    }
    
    // equals y hashCode basados en el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}