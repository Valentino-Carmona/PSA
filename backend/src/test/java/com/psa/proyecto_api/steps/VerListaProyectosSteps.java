package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for project listing scenarios.
 * 
 * This class handles:
 * - Setting up test data with existing projects
 * - Retrieving and validating project lists
 * - Verifying project list properties and structure
 * - Validating project summary information
 * 
 * Supports scenarios for empty project lists and populated project lists.
 */
public class VerListaProyectosSteps extends BaseCucumber {
    
    // Test data
    private List<ProjectSummaryResponse> projects;

    /**
     * Sets up the test environment before each scenario.
     */
    @Before
    public void setUp() {
        super.setUp();
    }

    /**
     * Clears all projects from the system to ensure an empty state.
     */
    @Given("no existen proyectos en el sistema")
    public void noExistenProyectosEnElSistema() {
        projectBuilder.clearProjects();
        assertTrue(projectBuilder.getProjects().isEmpty());
    }

    /**
     * Creates test projects based on the provided data table.
     * 
     * @param table Cucumber data table containing project information
     */
    @Given("existen los siguientes proyectos")
    public void existenLosSiguientesProyectos(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
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
        }        
    }

    /**
     * Retrieves the list of projects from the system.
     */
    @When("se solicita la lista de proyectos")
    public void elUsuarioSolicitaLaListaDeProyectos() {
        projects = projectBuilder.getProjects();
    }

    /**
     * Validates that the project list contains the expected number of projects.
     * 
     * @param expectedCount Expected number of projects in the list
     */
    @Then("se pueden ver {int} proyectos en la lista")
    public void sePuedenVerProyectosEnLaLista(int expectedCount) {
        assertEquals(expectedCount, projects.size());
    }
    
    /**
     * Validates that the project list contains projects with the specified names.
     * 
     * @param names List of expected project names
     */
    @And("están los proyectos con los siguientes nombres")
    public void estanLosProyectosConLosSiguientesNombres(List<String> names) {
        for (String name : names) {
            assertTrue(projects.stream().anyMatch(project -> project.getName().equals(name)));
        }
    }

    /**
     * Validates that each project in the list contains the required columns/data.
     * 
     * @param columns List of column names to validate
     */
    @And("cada proyecto debería mostrar las siguientes columnas")
    public void cadaProyectoDeberiaMostrarLasSiguientesColumnas(List<String> columns) {
        for (String column : columns) {
            for (ProjectSummaryResponse project : projects) {
                switch (column) {
                    case "id":
                        assertNotNull(project.getId());
                        break;
                    case "name":
                        assertNotNull(project.getName());
                        break;
                    case "clientId":
                        assertNotNull(project.getClientId());
                        break;
                    case "status":
                        assertNotNull(project.getStatus());
                        break;
                    case "startDate":
                        assertNotNull(project.getStartDate());
                        break;
                    case "endDate":
                        assertNotNull(project.getEndDate());
                        break;
                    case "tagNames":
                        assertTrue(project.getTagNames().isEmpty());
                        break;
                    default:
                        throw new IllegalArgumentException("Columna no válida: " + column);
                }
            }
        }
    }
}
