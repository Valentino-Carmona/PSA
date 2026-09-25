package com.psa.proyecto_api.model.enums;

import com.psa.proyecto_api.exception.OperationNotAllowedException;

/**
 * Enumeración que define los posibles estados de una tarea.
 */
public enum TaskStatus {
    /**
     * Tarea pendiente por hacer.
     */
    TO_DO("Por Hacer"),
    
    /**
     * Tarea en progreso.
     */
    IN_PROGRESS("En Progreso"),
    
    /**
     * Tarea completada.
     */
    DONE("Completada");
    
    private final String displayName;
    
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public static TaskStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        
        String trimmedValue = value.trim();
        
        // Try to match by display name first
        for (TaskStatus type : TaskStatus.values()) {
            if (type.displayName.equalsIgnoreCase(trimmedValue)) {
                return type;
            }
        }
        
        // Try to match by enum name
        try {
            return TaskStatus.valueOf(trimmedValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperationNotAllowedException("Tipo de estado no válido: " + value);
        }
    }
    
    /**
     * Verifica si el estado representa una tarea activa.
     */
    public boolean isActive() {
        return this == TO_DO || this == IN_PROGRESS;
    }
    
    /**
     * Verifica si la tarea puede ser iniciada desde este estado.
     */
    public boolean canStart() {
        return this == TO_DO;
    }
    
    /**
     * Verifica si la tarea puede ser completada desde este estado.
     */
    public boolean canComplete() {
        return this == TO_DO || this == IN_PROGRESS;
    }
}

