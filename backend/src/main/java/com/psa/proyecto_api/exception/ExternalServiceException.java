package com.psa.proyecto_api.exception;

// Excepción para cuando falla la comunicación con servicios externos
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
    
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
