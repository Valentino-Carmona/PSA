package com.psa.proyecto_api.exception;

// Excepción para cuando no se encuentra un proyecto
public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }
    
    public ProjectNotFoundException(Long id) {
        super("Proyecto con ID " + id + " no encontrado");
    }
}