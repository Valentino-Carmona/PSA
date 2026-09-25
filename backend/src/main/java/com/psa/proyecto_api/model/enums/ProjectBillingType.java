package com.psa.proyecto_api.model.enums;

import com.psa.proyecto_api.exception.OperationNotAllowedException;

public enum ProjectBillingType {
    
    /**
     * Proyecto de precio variable, donde el costo se basa en el tiempo y materiales utilizados.
     */
    TIME_AND_MATERIAL("Tiempo y Materiales"),
    
    /**
     * Proyecto de precio fijo, donde se establece un costo total por el proyecto.
     */
    FIXED_PRICE("Precio Fijo");
    
    private final String displayName;
    
    ProjectBillingType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public static ProjectBillingType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        String trimmedValue = value.trim();
        
        // Try to match by display name first
        for (ProjectBillingType type : ProjectBillingType.values()) {
            if (type.displayName.equalsIgnoreCase(trimmedValue)) {
                return type;
            }
        }
        
        // Try to match by enum name
        try {
            return ProjectBillingType.valueOf(trimmedValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperationNotAllowedException("Tipo de facturación no válido: " + value);
        }
    }

    /**
     * Verifica si el tipo de facturación es de precio fijo.
     */
    public boolean isFixedPrice() {
        return this == FIXED_PRICE;
    }

    /**
     * Verifica si el tipo de facturación es de tiempo y materiales.
     */
    public boolean isTimeAndMaterial() {
        return this == TIME_AND_MATERIAL;
    }
}
