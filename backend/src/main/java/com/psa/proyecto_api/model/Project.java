package com.psa.proyecto_api.model;

import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.exception.OperationNotAllowedException;
import com.psa.proyecto_api.exception.ResourceConflictException;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Entidad que representa un proyecto en el sistema.
 * 
 * Esta clase encapsula toda la información relacionada con un proyecto,
 * incluyendo sus tareas asociadas y tags.
 */
@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_projects_client_id", columnList = "client_id"),
    @Index(name = "idx_projects_type", columnList = "type"),
    @Index(name = "idx_projects_status", columnList = "status"),
    @Index(name = "idx_projects_leader_id", columnList = "leader_id"),
    @Index(name = "idx_projects_dates", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tasks", "tags"})
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull(message = "El tipo de proyecto es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProjectType type;

    @NotNull(message = "El tipo de facturación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false)
    private ProjectBillingType billingType;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @PositiveOrZero(message = "Las horas estimadas no pueden ser negativas")
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.INITIATED;
    
    // Relaciones externas
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "leader_id")
    @Size(min = 36, max = 36, message = "El id del lider debe tener 36 caracteres")
    private String leaderId;
    
    // Relaciones internas
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "project_tags",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
    

    // Constructor
    
    /**
     * Constructor para crear un proyecto con los campos obligatorios.
     */
    public Project(String name, Integer clientId, ProjectType type, ProjectBillingType billingType, LocalDate startDate) {
        this.verificateParameters(clientId);
        this.name = name;
        this.clientId = clientId;
        this.type = type;
        this.billingType = billingType;
        this.startDate = startDate;
        this.status = ProjectStatus.INITIATED;
        this.tasks = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.estimatedHours = 0;
    }

    // Metodos de gestion de datos basicos

    private void verificateParameters(Integer clientId) {
        if (clientId == null) {
            throw new OperationNotAllowedException("El cliente es obligatorio para todos los proyectos");
        }
    }

    public void addEndDate(LocalDate endDate) {
        if (endDate != null && ((endDate.isAfter(this.startDate)) || endDate.isEqual(this.startDate))) {
            this.endDate = endDate;
        }
    }

    public void addLeader(String leaderId) {
        if (leaderId != null && !leaderId.trim().isEmpty()) {
            this.leaderId = leaderId;
        }
    }

    public void statusSwitch() {
        if (!this.tasks.isEmpty()) {
            // Si hay almenos una tarea activa, el proyecto esta en progreso
            if (this.tasks.stream().anyMatch(Task::isActive)) {
                this.status = ProjectStatus.IN_PROGRESS;

            } else if (this.tasks.stream().allMatch(Task::isFinished)) {
                this.status = ProjectStatus.TRANSITION;
            }

        } else {
            this.status = ProjectStatus.INITIATED;
        }
    }

    // Métodos de gestión de tareas
    
    /**
     * Agrega una tarea al proyecto estableciendo la relación bidireccional.
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new OperationNotAllowedException("No se puede agregar una tarea nula al proyecto");
        }
        
        if (this.tasks.contains(task)) {
            throw new ResourceConflictException("La tarea ya está asociada a este proyecto");
        }
        
        tasks.add(task);
        this.updateProjectStatusAndHours();
    }

    /**
     * Remueve una tarea del proyecto.
     */
    public void removeTask(Task task) {
        if (task == null) {
            throw new OperationNotAllowedException("No se puede remover una tarea nula del proyecto");
        }
        
        if (!this.tasks.contains(task)) {
            throw new OperationNotAllowedException("La tarea no pertenece a este proyecto");
        }
        
        this.tasks.remove(task);
        this.updateProjectStatusAndHours();
    }
    
    /**
     * Agrega un tag al proyecto evitando duplicados. Recibe directamente la entidad Tag.
     */
    public void addTag(Tag tag) {
        if (tag == null) {
            throw new OperationNotAllowedException("El tag no puede ser nulo");
        }
                
        // Verificar si el tag ya existe
        boolean tagExists = tags.stream()
            .anyMatch(t -> t.getName().equalsIgnoreCase(tag.getName()));
            
        if (tagExists) {
            throw new ResourceConflictException("El tag '" + tag.getName() + "' ya existe en este proyecto");
        }
        
        tags.add(tag);
    }
    
    /**
     * Remueve un tag del proyecto.
     */
    public void removeTag(String tagName) {
        if (tagName == null || tagName.isEmpty()) {
            throw new OperationNotAllowedException("El nombre del tag no puede ser nulo o vacío");
        }
        
        boolean removed = tags.removeIf(tag -> tag.getName().equalsIgnoreCase(tagName.trim()));
        
        if (!removed) {
            throw new OperationNotAllowedException("El tag '" + tagName + "' no existe en este proyecto");
        }
    }

    
    // Metodos de actualizacion

    /**
     * Actualizar del proyecto
     */
    public void updateProjectStatusAndHours() {

        // Actualizar las horas estimadas del proyecto al agregar una tarea
        Integer newEstimatedHours = this.getTotalEstimatedHoursFromTasks();
        this.estimatedHours = newEstimatedHours.equals(this.estimatedHours) ? this.estimatedHours : newEstimatedHours;

        // Actualizar el estado del proyecto si es necesario
        this.statusSwitch();        
    }

    /**
     * Actualiza los detalles del proyecto.
     */
    public void updateDetails(String name, Integer clientId, String leaderId) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (leaderId != null && !leaderId.trim().isEmpty()) {
            this.leaderId = leaderId;
        }
    }

    /**
     * Actualiza los tipos del proyecto.
     */
    public void updateTypes(ProjectType type, ProjectBillingType billingType) {
        if (type != null) {
            this.type = type;
        }
        if (billingType != null) {
            this.billingType = billingType;
        }
    }

    /**
     * Actualiza las fechas del proyecto.
     */
    public void updateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null && ((endDate.isAfter(this.startDate)) || endDate.isEqual(this.startDate))) {
                this.endDate = endDate;
        } 
    }

    // Métodos de consulta de estado

    /**
     * Verifica si el proyecto está iniciado.
     */
    public boolean isInitiated() {
        return this.status.isInitiated();
    }

    /**
     * Verifica si el proyecto está activo (no completado).
     */
    public boolean isActive() {
        return this.status.isActive();
    }
    
    /**
     * Verifica si el proyecto está finalizado.
     */
    public boolean isFinished() {
        return this.status.isTransition();
    }
    
    // Métodos de cálculo
    
    /**
     * Calcula el total de horas estimadas en todas las tareas del proyecto.
     */
    public int getTotalEstimatedHoursFromTasks() {
        if (this.tasks.isEmpty()) {
            return 0;
        }
        return tasks.stream()
            .mapToInt(Task::getEstimatedHoursOrZero)
            .sum();
    }
    
    /**
     * Obtiene el porcentaje de progreso del proyecto basado en las tareas completadas.
     */
    public double getProgressPercentage() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        
        long completedTasks = tasks.stream()
            .mapToLong(Task::isFinishedAsLong)
            .sum();
            
        return (double) completedTasks / tasks.size() * 100.0;
    }
    
    /**
     * Obtiene la cantidad de tareas activas en el proyecto.
     */
    public long getActiveTasksCount() {
        return tasks.stream()
            .mapToLong(Task::isActiveAsLong)
            .sum();
    }
    
    /**
     * Obtiene la cantidad de tareas completadas en el proyecto.
     */
    public long getCompletedTasksCount() {
        return tasks.stream()
            .mapToLong(Task::isFinishedAsLong)
            .sum();
    }
    
    /**
     * Obtiene las tareas no asignadas del proyecto.
     */
    public List<Task> getUnassignedTasks() {
        return tasks.stream()
            .filter(task -> !task.isAssigned())
            .toList();
    }
    
    /**
     * Verifica si el proyecto tiene un tag específico.
     */
    public boolean hasTag(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            return false;
        }
        
        String normalizedTagName = tagName.trim();
        return tags.stream()
            .anyMatch(tag -> tag.getName().equalsIgnoreCase(normalizedTagName));
    }
    
    /**
     * Obtiene todos los nombres de tags del proyecto.
     */
    public List<String> getTagNames() {
        return tags.stream()
            .map(Tag::getName)
            .sorted()
            .toList();
    }
    
    /**
     * Busca una tarea por su ID.
     */
    public Optional<Task> findTaskById(Long taskId) {
        if (taskId == null) {
            return Optional.empty();
        }
        
        return tasks.stream()
            .filter(task -> task.hasId(taskId))
            .findFirst();
    }
    
    /**
     * Verifica si las fechas del proyecto son válidas.
     */
    @AssertTrue(message = "La fecha de fin debe ser posterior o igual a la fecha de inicio")
    private boolean isValidDateRange() {
        return endDate == null || endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }
    
    // equals y hashCode basados en el ID (para entidades JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return Objects.equals(id, project.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}