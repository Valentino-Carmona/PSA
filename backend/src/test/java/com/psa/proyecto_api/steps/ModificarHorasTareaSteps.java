package com.psa.proyecto_api.steps;

import java.util.Map;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.Assert.*;

public class ModificarHorasTareaSteps extends BaseCucumber {
    
    private Exception exception;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Given("existe una tarea con horas estimadas")
    public void existeUnaTareaConHorasEstimadas(DataTable table) {
        projectBuilder.createBasicProject();
        Map<String, String> data = table.asMaps(String.class, String.class).get(0);
        taskBuilder.createTask(
            projectBuilder.getProject().getId(), 
            data.get("name"),
            data.get("status"),
            data.get("ticketId"),
            data.get("estimatedHours"),
            data.get("assignedResourceId")
        );
        project = projectBuilder.getProject();
        task = taskBuilder.getTask();
    }

    @When("se solicita la modificación de las horas estimadas a {int}")
    public void seSolicitaLaModificacionDeLasHorasEstimadasA(Integer estimatedHours) {

        taskBuilder.updateTask();
        try {
            taskBuilder.modifyEstimatedHours(task.getId(), estimatedHours);
            task = taskBuilder.getTask();
            exception = null;
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("la tarea tiene {int} horas estimadas")
    public void laTareaTieneHorasEstimadas(Integer estimatedHours) {
        task = taskBuilder.getTask();
        assertEquals(estimatedHours, task.getEstimatedHours());
    }

    @Then("se rechaza la modificación de las horas estimadas")
    public void seRechazaLaModificacionDeLasHorasEstimadas() {
        assertNotNull(exception);
    }


}