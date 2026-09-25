package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import com.psa.proyecto_api.builders.ProjectTestDataBuilder;
import com.psa.proyecto_api.builders.TaskTestDataBuilder;
import com.psa.proyecto_api.model.enums.TaskStatus;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class CambiarEstadoTareaSteps extends BaseCucumber {
    
    @Before
    public void setUp() {
        super.setUp();
    }

    private Exception exception;

    @Given("cambiar estado de tarea: existe una tarea con estado {string}")
    public void cambiarEstadoDeTareaExisteUnaTareaConElEstado(String status) {
        project = projectBuilder.getOrCreateProject();
        task = projectBuilder.getOrCreateTaskWithStatus(status);
        assertNotNull(task);
        assertEquals(status, task.getStatus().toString());
    }

    @When("el usuario intenta iniciar la tarea")
    public void elUsuarioIntentaIniciarLaTarea() {
        exception = null;
        
        try {
            taskBuilder.activate(task);
            task = taskBuilder.getTask();
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario intenta completar la tarea")
    public void elUsuarioIntentaCompletarLaTarea() {
        exception = null;
        
        try {
            taskBuilder.deactivate(task);
            task = taskBuilder.getTask();
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("el usuario intenta retroceder la tarea")
    public void elUsuarioIntentaRetrocederLaTarea() {
        exception = null;
        
        try {
            taskBuilder.activate(task);
            task = taskBuilder.getTask();
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("la tarea tiene el estado {string}")
    public void laTareaTieneElEstado(String estado) {
        assertNotNull("La tarea no existe", task);
        TaskStatus expectedStatus = TaskStatus.valueOf(estado);
        assertEquals("El estado de la tarea no coincide", expectedStatus, task.getStatus());
    }

    @Then("se rechaza la operación por estado inválido")
    public void seRechazaLaOperacionPorEstadoInvalido() {
        assertTrue("Se esperaba que la operación fallara", 
            exception != null);
    }

    @Then("el estado de la tarea se mantiene en {string}")
    public void elEstadoDeLaTareaSeMantieneEn(String estado) {
        assertNotNull("La tarea no existe", task);
        TaskStatus expectedStatus = TaskStatus.valueOf(estado);
        assertEquals("El estado de la tarea cambió cuando no debería", expectedStatus, task.getStatus());
    }
}