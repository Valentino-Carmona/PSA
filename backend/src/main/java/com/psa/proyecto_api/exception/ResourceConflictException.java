package com.psa.proyecto_api.exception;

// Excepción para cuando hay conflictos de recursos
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
