package com.psa.proyecto_api.exception;

// Excepción para cuando no se puede realizar una operación
public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}