package com.psa.proyecto_api.service.impl;

import com.psa.proyecto_api.config.ExternalApiConfig;
import com.psa.proyecto_api.exception.ExternalServiceException;
import com.psa.proyecto_api.exception.OperationNotAllowedException;
import com.psa.proyecto_api.service.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExternalApiServiceImpl implements ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExternalApiConfig externalApiConfig;

    @Override
    public List<Map<String, Object>> getResources() {
        try {
            String url = externalApiConfig.getResourceApiUrl();

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return response.getBody();

        } catch (RestClientException e) {
            throw new ExternalServiceException("No se pudo consultar la API de recursos", e);
        }
    }

    @Override
    public List<Map<String, Object>> getClients() {
        try {
            String url = externalApiConfig.getClientsApiUrl();

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return response.getBody();

        } catch (RestClientException e) {
            throw new ExternalServiceException("No se pudo consultar la API de clientes", e);
        }
    }

    // @Override
    // public List<Map<String, Object>> getTicketsByTaskId(Long taskId) {
    //     try {
    //         String url = externalApiConfig.getSupportApiUrl() + "/tickets?taskId=" + taskId;

    //         ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
    //                 url,
    //                 HttpMethod.GET,
    //                 null,
    //                 new ParameterizedTypeReference<List<Map<String, Object>>>() {}
    //         );

    //         return response.getBody();

    //     } catch (RestClientException e) {
    //         throw new ExternalServiceException("No se pudo consultar los tickets para la tarea " + taskId, e);
    //     }
    // }

    @Override
    public Map<String, Object> getClientById(Integer clientId) {
        if (clientId == null || clientId < 0) {
            throw new OperationNotAllowedException("El ID del cliente no puede ser nulo o menor que cero");
        }
        List<Map<String, Object>> clients = this.getClients();

        return clients.stream()
                .filter(client -> clientId.equals(client.get("id")))
                .findFirst()
                .orElseThrow(() -> new ExternalServiceException("No se encontró ningún cliente con ID: " + clientId));
    }

    @Override
    public Map<String, Object> getResourceById(String resourceId) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            throw new OperationNotAllowedException("El ID del recurso no puede ser nulo o vacio");
        }
        List<Map<String, Object>> resources = this.getResources();

        return resources.stream()
                .filter(resource -> resourceId.equals(resource.get("id")))
                .findFirst()
                .orElseThrow(() -> new ExternalServiceException("No se encontró ningún recurso con ID: " + resourceId));
    }
}
