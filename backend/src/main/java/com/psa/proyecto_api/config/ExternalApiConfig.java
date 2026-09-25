package com.psa.proyecto_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalApiConfig {

    // URLs de las APIs externas (desde application.properties o .env)
    @Value("${RESOURCES_API_URL}")
    private String resourcesApiUrl;

    @Value("${CLIENTS_API_URL}")
    private String clientsApiUrl;

    // @Value("${SUPPORT_API_URL}")
    // private String supportApiUrl;

    // Getters para usar en los servicios
    public String getResourceApiUrl() {
        return resourcesApiUrl;
    }

    public String getClientsApiUrl() {
        return clientsApiUrl;
    }

    // public String getSupportApiUrl() {
    //     return supportApiUrl;
    // }
}
