package com.psa.proyecto_api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI("/api/v1/test");
        webRequest = new ServletWebRequest(httpRequest);
    }

    @Test
    void handleProjectNotFoundException_Returns404() {
        ProjectNotFoundException ex = new ProjectNotFoundException(42L);

        ResponseEntity<ErrorResponse> response = handler.handleProjectNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    void handleTaskNotFoundException_Returns404() {
        TaskNotFoundException ex = new TaskNotFoundException(7L);

        ResponseEntity<ErrorResponse> response = handler.handleTaskNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleInvalidProjectStatusException_Returns400() {
        InvalidProjectStatusException ex = new InvalidProjectStatusException("INVALID");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidProjectStatusException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
    }

    @Test
    void handleOperationNotAllowedException_Returns403() {
        OperationNotAllowedException ex = new OperationNotAllowedException("No permitido");

        ResponseEntity<ErrorResponse> response = handler.handleOperationNotAllowedException(ex, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden", response.getBody().getError());
    }

    @Test
    void handleExternalServiceException_Returns503() {
        ExternalServiceException ex = new ExternalServiceException("API caída");

        ResponseEntity<ErrorResponse> response = handler.handleExternalServiceException(ex, webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().getStatus());
        assertEquals("Service Unavailable", response.getBody().getError());
    }

    @Test
    void handleMethodArgumentNotValidException_Returns400WithValidationErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "name", "El nombre es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertNotNull(response.getBody().getValidationErrors());
        assertTrue(response.getBody().getValidationErrors().containsKey("name"));
    }

    @Test
    void handleIllegalArgumentException_Returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Argumento inválido", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_Returns500() {
        Exception ex = new RuntimeException("Fallo inesperado");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    void handleProjectNotFoundException_IncludesPathInResponse() {
        ProjectNotFoundException ex = new ProjectNotFoundException(1L);

        ResponseEntity<ErrorResponse> response = handler.handleProjectNotFoundException(ex, webRequest);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPath());
        assertFalse(response.getBody().getPath().isEmpty());
    }

    @Test
    void handleExternalServiceException_WithCause_Returns503() {
        ExternalServiceException ex = new ExternalServiceException("Timeout", new RuntimeException("connection refused"));

        ResponseEntity<ErrorResponse> response = handler.handleExternalServiceException(ex, webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }
}
