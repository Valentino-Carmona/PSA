package com.psa.proyecto_api.steps;

import com.psa.proyecto_api.steps.BaseCucumber;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FiltrarProyectosSteps extends BaseCucumber {
    List<ProjectSummaryResponse> projects;
    Exception exception;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        super.setUp();
    }

    @Given("existen los siguientes proyectos para buscar")
    public void existenLosSiguientesProyectosParaBuscar(DataTable table) {
        projectBuilder.clearProjects();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            List<String> tags = new ArrayList<>();
            if (row.containsKey("tags") && row.get("tags") != null && !row.get("tags").isEmpty()) {
                try {
                    tags = objectMapper.readValue(row.get("tags"), new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    // If JSON parsing fails, treat as single tag
                    tags.add(row.get("tags"));
                }
            }
            
            projectBuilder.createProject(
                row.get("name"),
                Integer.parseInt(row.get("clientId")),
                ProjectType.valueOf(row.get("type")),
                ProjectBillingType.valueOf(row.get("billingType")),
                LocalDate.parse(row.get("startDate")),
                LocalDate.parse(row.get("endDate")),
                ProjectStatus.valueOf(row.get("status")),
                row.get("leaderId")
            );
            
            // Add tags if any
            if (!tags.isEmpty()) {
                projectBuilder.addTagsToProject(tags);
            }
        }  
    }

    @When("el usuario busca proyectos por nombre completo {string}")
    public void elUsuarioBuscaProyectosPorNombreCompleto(String name) {
        String filter = "?nombre=" + name;
        try {
            projects = projectBuilder.getProjectsBy(filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario busca proyectos por nombre parcial {string}")
    public void elUsuarioBuscaProyectosPorNombreParcial(String name) {
        String filter = "?nombre=" + name;
        try {
            projects = projectBuilder.getProjectsBy(filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario filtra los proyectos por tipo {string}")
    public void elUsuarioFiltraLosProyectosPorTipo(String type) {
        String filter = "?tipo=" + type;
        try {
            projects = projectBuilder.getProjectsBy(filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario filtra los proyectos por estado {string}")
    public void elUsuarioFiltraLosProyectosPorEstado(String status) {
        String filter = "?estado=" + status;
        try {
            projects = projectBuilder.getProjectsBy(filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario filtra los proyectos por tag {string}")
    public void elUsuarioFiltraLosProyectosPorTag(String tag) {
        String filter = "?tag=" + tag;
        try {
            projects = projectBuilder.getProjectsBy(filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario aplica los siguientes filtros de proyecto:")
    public void elUsuarioAplicaLosSiguientesFiltrosDeProyecto(DataTable dataTable) {
        List<Map<String, String>> filters = dataTable.asMaps(String.class, String.class);
        StringBuilder filter = new StringBuilder("?");
        
        Map<String, String> filterMap = filters.get(0);
        boolean first = true;
        
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            if (!first) {
                filter.append("&");
            }
            filter.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        
        try {
            projects = projectBuilder.getProjectsBy(filter.toString());
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("debe obtener exactamente {int} proyectos por la busqueda")
    public void debeObtenerExactamenteProyectosPorLaBusqueda(int expectedSize) {
        assertEquals(expectedSize, projects.size());
    }

    @Then("debe obtener exactamente {int} proyectos")
    public void debeObtenerExactamenteProyectos(int expectedSize) {
        assertEquals(expectedSize, projects.size());
    }

    @And("el proyecto obtenido debe ser {string}")
    public void elProyectoObtenidoDebeSer(String name) {
        assertEquals(name, projects.get(0).getName());
    }

    @And("los proyectos obtenidos deben ser:")
    public void losProyectosObtenidosDebenSer(DataTable dataTable) {
        List<Map<String, String>> expectedProjectRows = dataTable.asMaps(String.class, String.class);
        List<String> expectedProjectNames = expectedProjectRows.stream()
            .map(row -> row.get("name"))
            .collect(Collectors.toList());

        List<String> actualProjectNames = projects.stream()
            .map(ProjectSummaryResponse::getName)
            .collect(Collectors.toList());

        for (String expectedName : expectedProjectNames) {
            assertTrue("Expected project name not found: " + expectedName, 
                      actualProjectNames.contains(expectedName));
        }
    }

    @Then("debe obtener todos los proyectos del sistema por la busqueda")
    public void debeObtenerTodosLosProyectosDelSistemaPorLaBusqueda() {
        assertTrue(projects.size() >= 5); // At least the 5 projects from background
    }
} 