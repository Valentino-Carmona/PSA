package com.psa.proyecto_api.exception;

// Excepción para cuando no se encuentra una tarea
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
    
    public TaskNotFoundException(Long id) {
        super("Tarea con ID " + id + " no encontrada");
    }
}
