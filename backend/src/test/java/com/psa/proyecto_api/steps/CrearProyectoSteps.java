package com.psa.proyecto_api.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for project creation scenarios.
 * 
 * This class handles:
 * - Creating projects with complete information
 * - Creating projects with minimal required information
 * - Validating project creation with incomplete data
 * - Verifying project properties after creation
 * 
 * Supports various project types, billing types, and validation scenarios.
 */
public class CrearProyectoSteps {

    // Configuration and dependencies
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // API configuration
    private String baseUrl;
    private static final String API_URL = "/api/v1/proyectos";

    // Test data and responses
    private CreateProjectRequest projectRequest;
    private ResponseEntity<ProjectResponse> response;
    private ProjectResponse project;

    /**
     * Initializes the base URL for API calls before each test scenario.
     */
    @Before
    public void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    /**
     * Sets up project information from a data table for complete project creation.
     * 
     * @param dataTable Cucumber data table containing project information
     */
    @Given("se tiene la siguiente información del proyecto:")
    public void seTieneLaSiguienteInformacionDelProyecto(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        Map<String, String> map = data.get(0);
        
        projectRequest = new CreateProjectRequest();
        projectRequest.setName(map.get("name"));
        projectRequest.setClientId(Integer.parseInt(map.get("clientId")));
        projectRequest.setType(ProjectType.valueOf(map.get("type")));
        projectRequest.setBillingType(ProjectBillingType.valueOf(map.get("billingType")));
        projectRequest.setStartDate(LocalDate.parse(map.get("startDate")));

        // Optional fields
        if (map.get("endDate") != null) {
            projectRequest.setEndDate(LocalDate.parse(map.get("endDate")));
        }
        if (map.get("leaderId") != null) {
            projectRequest.setLeaderId(map.get("leaderId"));
        }    
    }

    /**
     * Sets up incomplete project information for validation testing.
     * 
     * @param dataTable Cucumber data table containing partial project information
     */
    @Given("se tiene la siguiente información incompleta del proyecto:")
    public void seTieneLaSiguienteInformacionIncompletaDelProyecto(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        Map<String, String> map = data.get(0);
        
        projectRequest = new CreateProjectRequest();
        
        // Set fields only if they are provided
        if (map.get("name") != null) {
            projectRequest.setName(map.get("name"));
        }
        if (map.get("clientId") != null) {
            projectRequest.setClientId(Integer.parseInt(map.get("clientId")));
        }
        if (map.get("type") != null) {
            projectRequest.setType(ProjectType.valueOf(map.get("type")));
        }
        if (map.get("billingType") != null) {
            projectRequest.setBillingType(ProjectBillingType.valueOf(map.get("billingType")));
        }
        if (map.get("startDate") != null) {
            projectRequest.setStartDate(LocalDate.parse(map.get("startDate")));
        }
    }

    /**
     * Sends the project creation request to the API.
     */
    @When("se envía la solicitud para crear el proyecto")
    public void seEnviaLaSolicitudParaCrearElProyecto() {
        try {
            response = restTemplate.postForEntity(
                baseUrl + API_URL, projectRequest, ProjectResponse.class);
            project = response.getBody();
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
        }
    }

    /**
     * Validates that the project was created successfully with the expected data.
     * 
     * @param dataTable Cucumber data table containing expected project data
     */
    @Then("el proyecto debería ser creado exitosamente con los siguientes datos:")
    public void elProyectoDeberiaSerCreadoExitosamenteConLosSiguientesDatos(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        Map<String, String> map = data.get(0);

        assertNotNull(project, "Project should not be null");
        
        // Validate required fields
        assertEquals(map.get("name"), project.getName());
        assertEquals(Integer.parseInt(map.get("clientId")), project.getClientId());
        assertEquals(ProjectType.valueOf(map.get("type")), project.getType());
        assertEquals(ProjectBillingType.valueOf(map.get("billingType")), project.getBillingType());
        assertEquals(map.get("startDate"), project.getStartDate().toString());
        
        // Validate optional fields
        if (map.get("endDate") != null) {
            assertEquals(map.get("endDate"), project.getEndDate().toString());
        }
        if (map.get("leaderId") != null) {
            assertEquals(map.get("leaderId"), project.getLeaderId());
        }
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    /**
     * Validates that the created project has a unique ID.
     */
    @SuppressWarnings("unchecked")
    @Then("el proyecto debería tener un ID único")
    public void elProyectoDeberiaTenerUnIdUnico() {
        ResponseEntity<List> getResponse = restTemplate.getForEntity(baseUrl + API_URL, List.class);
        List<Map<String, Object>> projects = getResponse.getBody();

        int idFrequency = 0;
        for (Map<String, Object> projectMap : projects) {
            if (Long.valueOf(projectMap.get("id").toString()).equals(project.getId())) {
                idFrequency++;
            }
        }

        assertNotNull(project.getId(), "Project ID should not be null"); 
        assertEquals(1, idFrequency, "Project ID should exist in the list of projects and be unique");
    }

    /**
     * Validates that the project has the expected initial status.
     * 
     * @param expectedStatus Expected project status as string
     */
    @Then("el proyecto debería tener un estado inicial {string}")
    public void elProyectoDeberiaTenerUnEstadoInicial(String expectedStatus) {
        assertNotNull(project, "Project should not be null");
        assertEquals(ProjectStatus.valueOf(expectedStatus), project.getStatus(), 
            "Project status should be " + expectedStatus);
    }

    /**
     * Validates that project creation was rejected due to invalid data.
     */
    @Then("se rechaza la creación del proyecto")
    public void seRechazaLaCreacionDelProyecto() {
        assertNull(project, "Project should be null for invalid data");
        assertNull(response, "Response should be null");
    }

    /**
     * Validates that the project has zero estimated hours initially.
     */
    @Then("el proyecto debería tener 0 horas estimadas")
    public void elProyectoDeberiaTenerHorasEstimadas() {
        assertNotNull(project, "Project should not be null");
        assertEquals(0, project.getEstimatedHours(), 
            "Project estimated hours should be 0");
    }
}