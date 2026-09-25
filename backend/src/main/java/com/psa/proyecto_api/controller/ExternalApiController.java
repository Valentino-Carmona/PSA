package com.psa.proyecto_api.controller;

import com.psa.proyecto_api.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ExternalApiController {

    @Autowired
    private ExternalApiService externalApiService;

    // GET /recursos - Consultar recursos disponibles (pool de desarrolladores)
    @GetMapping("/recursos")
    public ResponseEntity<List<Map<String, Object>>> getRecursos() {
        List<Map<String, Object>> recursos = externalApiService.getResources();
        return ResponseEntity.ok(recursos);
    }

    // GET /recursos/{id} - Consultar clientes disponibles
    @GetMapping("/recursos/{id}")
    public ResponseEntity<Map<String, Object>> getResourceById(@PathVariable String id) {
        Map<String, Object> client = externalApiService.getResourceById(id);
        return ResponseEntity.ok(client);
    }

    // GET /clientes - Consultar clientes disponibles
    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> getClientes() {
        List<Map<String, Object>> clientes = externalApiService.getClients();
        return ResponseEntity.ok(clientes);
    }

    // GET /clientes/{id} - Consultar clientes disponibles
    @GetMapping("/clientes/{id}")
    public ResponseEntity<Map<String, Object>> getClientById(@PathVariable Integer id) {
        Map<String, Object> client = externalApiService.getClientById(id);
        return ResponseEntity.ok(client);
    }

}
