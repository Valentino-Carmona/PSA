package com.psa.proyecto_api.service;

import com.psa.proyecto_api.config.ExternalApiConfig;
import com.psa.proyecto_api.exception.ExternalServiceException;
import com.psa.proyecto_api.exception.OperationNotAllowedException;
import com.psa.proyecto_api.service.impl.ExternalApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExternalApiConfig externalApiConfig;

    @InjectMocks
    private ExternalApiServiceImpl externalApiService;

    private List<Map<String, Object>> mockResources;
    private List<Map<String, Object>> mockClients;

    @BeforeEach
    void setUp() {
        mockResources = List.of(
                Map.of("id", "123e4567-e89b-12d3-a456-426614174001", "name", "Recurso 1"),
                Map.of("id", "123e4567-e89b-12d3-a456-426614174002", "name", "Recurso 2")
        );

        mockClients = List.of(
                Map.of("id", 1, "name", "Cliente 1"),
                Map.of("id", 2, "name", "Cliente 2")
        );
    }

    @Test
    void getResources_Success() {
        String url = "http://mock-resources-api.com";
        when(externalApiConfig.getResourceApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResources, HttpStatus.OK));

        List<Map<String, Object>> resources = externalApiService.getResources();

        assertNotNull(resources);
        assertEquals(2, resources.size());
        assertEquals("Recurso 1", resources.get(0).get("name"));
    }

    @Test
    void getResources_ThrowsExternalServiceException_OnRestClientException() {
        String url = "http://mock-resources-api.com";
        when(externalApiConfig.getResourceApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Connection Refused"));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            externalApiService.getResources();
        });

        assertTrue(exception.getMessage().contains("No se pudo consultar la API de recursos"));
    }

    @Test
    void getClients_Success() {
        String url = "http://mock-clients-api.com";
        when(externalApiConfig.getClientsApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockClients, HttpStatus.OK));

        List<Map<String, Object>> clients = externalApiService.getClients();

        assertNotNull(clients);
        assertEquals(2, clients.size());
        assertEquals(1, clients.get(0).get("id"));
    }

    @Test
    void getClients_ThrowsExternalServiceException_OnRestClientException() {
        String url = "http://mock-clients-api.com";
        when(externalApiConfig.getClientsApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Timeout"));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            externalApiService.getClients();
        });

        assertTrue(exception.getMessage().contains("No se pudo consultar la API de clientes"));
    }

    @Test
    void getClientById_Success() {
        String url = "http://mock-clients-api.com";
        when(externalApiConfig.getClientsApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockClients, HttpStatus.OK));

        Map<String, Object> client = externalApiService.getClientById(1);

        assertNotNull(client);
        assertEquals("Cliente 1", client.get("name"));
    }

    @Test
    void getClientById_ThrowsOperationNotAllowedException_WhenIdIsNull() {
        assertThrows(OperationNotAllowedException.class, () -> {
            externalApiService.getClientById(null);
        });
    }

    @Test
    void getClientById_ThrowsOperationNotAllowedException_WhenIdIsNegative() {
        assertThrows(OperationNotAllowedException.class, () -> {
            externalApiService.getClientById(-1);
        });
    }

    @Test
    void getClientById_ThrowsExternalServiceException_WhenClientNotFound() {
        String url = "http://mock-clients-api.com";
        when(externalApiConfig.getClientsApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockClients, HttpStatus.OK));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            externalApiService.getClientById(99);
        });

        assertTrue(exception.getMessage().contains("con ID: 99"));
    }

    @Test
    void getResourceById_Success() {
        String url = "http://mock-resources-api.com";
        when(externalApiConfig.getResourceApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResources, HttpStatus.OK));

        Map<String, Object> resource = externalApiService.getResourceById("123e4567-e89b-12d3-a456-426614174001");

        assertNotNull(resource);
        assertEquals("Recurso 1", resource.get("name"));
    }

    @Test
    void getResourceById_ThrowsOperationNotAllowedException_WhenIdIsNull() {
        assertThrows(OperationNotAllowedException.class, () -> {
            externalApiService.getResourceById(null);
        });
    }

    @Test
    void getResourceById_ThrowsOperationNotAllowedException_WhenIdIsEmpty() {
        assertThrows(OperationNotAllowedException.class, () -> {
            externalApiService.getResourceById("   ");
        });
    }

    @Test
    void getResourceById_ThrowsExternalServiceException_WhenResourceNotFound() {
        String url = "http://mock-resources-api.com";
        when(externalApiConfig.getResourceApiUrl()).thenReturn(url);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResources, HttpStatus.OK));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            externalApiService.getResourceById("unknown-id");
        });

        assertTrue(exception.getMessage().contains("con ID: unknown-id"));
    }
}
