package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import com.psa.proyecto_api.builders.ProjectTestDataBuilder;
import com.psa.proyecto_api.builders.TaskTestDataBuilder;
import com.psa.proyecto_api.dto.response.TaskResponse;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MonitorearProgresoProyectoSteps extends BaseCucumber {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Given("existe un proyecto con estado {string}")
    public void existeUnProyectoConEstado(String status) {
        projectBuilder.createBasicProjectWithStatus(status);
        project = projectBuilder.getProject();
        assertNotNull(project);
        assertEquals(status, project.getStatus().toString());
    }

    @Given("existe una tarea con el estado {string}")
    public void existeUnaTareaConElEstado(String status) {
        project = projectBuilder.getOrCreateProject();
        task = projectBuilder.getOrCreateTaskWithStatus(status);
        assertNotNull(task);
        assertEquals(status, task.getStatus().toString());
    }

    @When("el usuario inicia la tarea")
    public void elUsuarioIniciaLaTarea() {
        taskBuilder.activate(task);
    }

    @Then("la tarea cambia a estado {string}")
    public void laTareaCambiaAEstado(String status) {
        task = taskBuilder.getTask();
        assertEquals(status, task.getStatus().toString());
    }

    @Then("el proyecto actualiza su estado a {string}")
    public void elProyectoActualizaSuEstadoA(String status) {
        project = projectBuilder.getProject();
        assertEquals(status, project.getStatus().toString());
    }

    @When("el usuario completa la tarea")
    public void elUsuarioCompletaLaTarea() {
        taskBuilder.deactivate(task);
    }

    @Given("existen tareas con los siguientes estados:")

    public void existenTareasConLosSiguientesEstados(DataTable dataTable) {
        project = projectBuilder.getProject();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String taskName = row.get("taskName");
            String status = row.get("status");
            taskBuilder.createTaskWithStatus(taskName, project.getId(), status);
        }
        project = projectBuilder.getProject();
        assertNotNull(project);
        assertEquals(rows.size(), project.getTasks().size());
        for (Map<String, String> row : rows) {
            TaskResponse task = projectBuilder.getTaskWithName(row.get("taskName"));
            assertEquals(row.get("status"), task.getStatus().toString());
        }
    }

    @When("el usuario completa la tarea {string}")
    public void elUsuarioCompletaLaTarea(String taskName) {
        task = projectBuilder.getTaskWithName(taskName);
        taskBuilder.deactivate(task);
    }

    @Then("la tarea {string} cambia a estado {string}")
    public void laTareaCambiaAEstado(String taskName, String status) {
        task = projectBuilder.getTaskWithName(taskName);
        assertEquals(status, task.getStatus().toString());
    }

    @When("completa todas sus tareas")
    public void completaTodasSusTareas() {
        project = projectBuilder.getProject();
        for (TaskResponse task : project.getTasks()) {
            taskBuilder.deactivate(task);
        }
    }

    @Then("todas las tareas cambian a estado {string}")
    public void todasLasTareasCambianAEstado(String status) {
        project = projectBuilder.getProject();
        for (TaskResponse task : project.getTasks()) {
            assertEquals(status, task.getStatus().toString());
        }
    }

    @Given("no existen tareas para el proyecto")
    public void noExistenTareasParaElProyecto() {
        project = projectBuilder.getProject();
        assertTrue(project.getTasks().isEmpty());
    }

    @Then("el proyecto debe tener estado {string}")
    public void elProyectoDebeTenerEstado(String status) {
        project = projectBuilder.getProject();
        assertEquals(status, project.getStatus().toString());
    }
}