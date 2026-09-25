package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.steps.BaseCucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;


public class EliminarProyectoSteps extends BaseCucumber {
    
    private Exception exception;
    private List<TaskResponse> tasks = new ArrayList<>();

    @Before
    @Override
    public void setUp() {
        super.setUp();
    }

    @Given("existe un proyecto")
    public void existeUnProyecto() {
        projectBuilder.createBasicProject();
        project = projectBuilder.getProject();
        assertNotNull(project);
    }

    @When("se solicita la eliminación del proyecto")
    public void seSolicitaLaEliminacionDelProyecto() {
        exception = null;
        try {
            projectBuilder.deleteProject(project.getId());
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("el proyecto se elimina correctamente")
    public void elProyectoSeEliminaCorrectamente() {
        assertThrows(Exception.class, 
        () -> projectBuilder.getProject(project.getId()));
    }

    @Given("existen las siguientes tareas en el proyecto antes de eliminarlo")
    public void existenLasSiguientesTareasEnElProyectoAntesDeEliminarlo(DataTable table) {
        project = projectBuilder.getProject();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            taskBuilder.createTaskWithStatus(
                row.get("name"),
                project.getId(),
                row.get("status")
            );
            tasks.add(taskBuilder.getTask());
        }
        assertEquals(3, tasks.size());
    }

    @Then("las tareas del proyecto se eliminan correctamente")
    public void lasTareasDelProyectoSeEliminaCorrectamente() {
        for (TaskResponse task : tasks) {
            assertThrows(Exception.class, 
            () -> taskBuilder.getTask(task.getId()));
        }
    }

    @Given("no existe un proyecto de id {long}")
    public void noExisteUnProyectoDeId(Long id) {
        assertThrows(Exception.class, 
        () -> projectBuilder.getProject(id));
    }


    @Then("se rechaza la solicitud porque el proyecto no existe")
    public void seRechazaLaSolicitudPorqueElProyectoNoExiste() {
        assertNotNull(exception);
    }
}
