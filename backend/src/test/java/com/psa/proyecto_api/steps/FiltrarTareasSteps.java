package com.psa.proyecto_api.steps;

import com.psa.proyecto_api.steps.BaseCucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.psa.proyecto_api.dto.response.TaskResponse;


public class FiltrarTareasSteps extends BaseCucumber {

    List<TaskResponse> tasks;
    Exception exception;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Given("existe un proyecto con las siguientes tareas con nombres unicos:")
    public void existeUnProyectoConLasSiguientesTareasConNombresUnicos(DataTable dataTable) {
        projectBuilder.createBasicProject();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            taskBuilder.createTask(projectBuilder.getProject().getId(), row.get("name"), row.get("status"), row.get("ticketId"));            
        }
    }

    @When("el usuario filtra las tareas por estado {string}")
    public void elUsuarioFiltraLasTareasPorEstado(String status) {
        String filter = "?estado=" + status;
        try {
            tasks = taskBuilder.getTasksBy(projectBuilder.getProject().getId(), filter);
        } catch (Exception e) {
            exception = e;
        }
        
    }

    @Then("debe obtener exactamente {int} tareas")
    public void debeObtenerExactamente3Tareas(int expectedSize) {
        assertEquals(expectedSize, tasks.size());        
    }

    @And("las tareas obtenidas deben ser:")
    public void lasTareasObtenidasDebenSer(DataTable dataTable) {
        // Extract expected task names from data table
        List<Map<String, String>> expectedTaskRows = dataTable.asMaps(String.class, String.class);
        List<String> expectedTaskNames = expectedTaskRows.stream()
            .map(row -> row.get("name"))
            .collect(Collectors.toList());

        List<String> actualTaskNames = tasks.stream()
            .map(task -> task.getName())
            .collect(Collectors.toList());

        for (String expectedName : expectedTaskNames) {
            assertTrue(actualTaskNames.contains(expectedName));
        }
    }

    @And("la operación se rechaza por estado no válido")
    public void laOperacionSeRechazaPorEstadoNoValido() {
        assertNotNull(exception);
    }


    @When("el usuario filtra las tareas por ticketId {string}")
    public void elUsuarioFiltraLasTareasPorTicketId(String ticketId) {
        String filter = "?ticketId=" + ticketId;
        try {
            tasks = taskBuilder.getTasksBy(projectBuilder.getProject().getId(), filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario busca tareas por nombre completo {string}")
    public void elUsuarioBuscaTareasPorNombreCompleto(String name) {
        String filter = "?nombre=" + name;
        try {
            tasks = taskBuilder.getTasksBy(projectBuilder.getProject().getId(), filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario busca tareas por nombre parcial {string}")
    public void elUsuarioBuscaTareasPorNombreParcial(String name) {
        String filter = "?nombre=" + name;
        try {
            tasks = taskBuilder.getTasksBy(projectBuilder.getProject().getId(), filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    @And("la tarea obtenida debe ser {string}")
    public void laTareaObtenidaDebeSer(String name) {
        assertEquals(name, tasks.get(0).getName());
    }

    @Then("debe obtener todas las tareas del proyecto")
    public void debeObtenerTodasLasTareasDelProyecto() {
        Integer expectedSize = projectBuilder.getProject().getTasks().size();
        assertTrue(expectedSize == tasks.size());
    }

    @When("el usuario aplica los siguientes filtros:")
    public void elUsuarioAplicaLosSiguientesFiltros(DataTable dataTable) {
        List<Map<String, String>> filters = dataTable.asMaps(String.class, String.class);
        String filter = "";
        filter = filter + "?estado=" + filters.get(0).get("status");
        filter = filter + "&ticketId=" + filters.get(0).get("ticketId");
        try {
            tasks = taskBuilder.getTasksBy(projectBuilder.getProject().getId(), filter);
        } catch (Exception e) {
            exception = e;
        }
    }

    
}
