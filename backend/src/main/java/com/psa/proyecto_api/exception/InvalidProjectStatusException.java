package com.psa.proyecto_api.exception;

// Excepción para estado de proyecto inválido
public class InvalidProjectStatusException extends RuntimeException {
    public InvalidProjectStatusException(String message) {
        super(message);
    }
    
    public InvalidProjectStatusException(String currentStatus, String attemptedStatus) {
        super("No se puede cambiar el estado del proyecto de " + currentStatus + " a " + attemptedStatus);
    }
}
