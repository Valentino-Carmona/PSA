package com.psa.proyecto_api.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.Assert.*;

import com.psa.proyecto_api.steps.BaseCucumber;

public class AsignarRecursoTareaSteps extends BaseCucumber {
    
    Long taskId;
    String previousResourceId = "11111111-1111-1111-1111-111111111111";
    String assignedResourceId;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Given("existe la siguiente tarea sin recurso asignado")
    public void existeLaSiguienteTareaSinRecursoAsignado() {
        projectBuilder.createBasicProject();
        taskBuilder.clearTasks();
        project = projectBuilder.getProject();
        assertTrue("La lista de tareas debería estar vacía", project.getTasks().isEmpty());

        taskBuilder.createBasicTask(project.getId());
        task = taskBuilder.getTask();
        assertNotNull("La tarea debería existir", task);
        taskId = taskBuilder.getTask().getId();
        project = projectBuilder.getProject();
    }

    @When("se solicita la asignación del recurso {string} a la tarea")
    public void seSolicitaLaAsignacionDeUnRecursoATarea(String resourceId) {
        this.assignedResourceId = resourceId;
        taskBuilder.assignResourceToTask(task.getId(), resourceId);
    }

    @Then("el recurso se asigna correctamente a la tarea")
    public void elRecursoSeAsignaCorrectamenteATarea() {
        task = taskBuilder.getTask(taskId);
        assertNotNull("La tarea debería existir", task);

        assertEquals("El recurso debería estar asignado", assignedResourceId, task.getAssignedResourceId());
    }

    @Given("existe la siguiente tarea con recurso asignado")
    public void existeLaSiguienteTareaConRecursoAsignado() {
        projectBuilder.createBasicProject();
        taskBuilder.clearTasks();
        project = projectBuilder.getProject();
        assertTrue("La lista de tareas debería estar vacía", project.getTasks().isEmpty());

        taskBuilder.createTaskWithAssignedResource(project.getId(), previousResourceId);
        task = taskBuilder.getTask();
        assertNotNull("La tarea debería existir", task);
        assertEquals("El recurso debería estar asignado", previousResourceId, task.getAssignedResourceId());
        taskId = taskBuilder.getTask().getId();
        project = projectBuilder.getProject();
    }

    
}
