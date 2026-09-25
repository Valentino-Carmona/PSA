package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import com.psa.proyecto_api.model.enums.TaskStatus;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class VerListaTareasSteps extends BaseCucumber {
    
    private List<TaskSummaryResponse> tasks;
    private Long projectId;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Given("existe un proyecto vacio")
    public void existeUnProyectoVacio() {
        projectBuilder.createBasicProject();
        taskBuilder.clearTasks();
        project = projectBuilder.getProject();
        assertTrue("La lista de tareas debería estar vacía", project.getTasks().isEmpty());
    }

    @Given("existen las siguientes tareas en el proyecto")
    public void existenLasSiguientesTareasEnElProyecto(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            taskBuilder.createTask(
                project.getId(),
                row.get("name"),
                row.get("status"),
                row.get("ticketId")
            );
        }        
        project = projectBuilder.getProject();
    }

    @When("se solicita la lista de tareas del proyecto")
    public void seSolicitaLaListaDeTareasDelProyecto() {
        tasks = taskBuilder.getTasksSummaryBy(project.getId(), "");
    }

    @Then("se pueden ver {int} tareas en la lista")
    public void sePuedenVerTareasEnLaLista(int expectedCount) {
        assertEquals(expectedCount, tasks.size());
    }
    
    @And("están las tareas con los siguientes nombres")
    public void estanLasTareasConLosSiguientesNombres(List<String> names) {
        for (String name : names) {
            assertTrue(tasks.stream().anyMatch(task -> task.getName().equals(name)));
        }
    }

    @And("cada tarea debería mostrar las siguientes columnas")
    public void cadaTareaDeberiaMostrarLasSiguientesColumnas(List<String> columns) {
        for (String column : columns) {
            for (TaskSummaryResponse task : tasks) {
                switch (column) {
                    case "id":
                        assertNotNull(task.getId());
                        break;
                    case "name":
                        assertNotNull(task.getName());
                        break;
                    case "projectId":
                        assertNotNull(task.getProjectId());
                        break;
                    case "ticketId":
                        assertNotNull(task.getTicketId());
                        break;
                    case "status":
                        assertNotNull(task.getStatus());
                        break;
                    case "estimatedHours":
                        assertNotNull(task.getEstimatedHours());
                        break;
                    case "assignedResourceId":
                        // This field is optional, so just check that the property exists (no assertion needed)
                        break;
                    case "tagNames":
                        assertTrue(task.getTagNames().isEmpty());
                        break;
                    default:
                        throw new IllegalArgumentException("Columna no válida: " + column);
                }
            }
        }
    }
} 