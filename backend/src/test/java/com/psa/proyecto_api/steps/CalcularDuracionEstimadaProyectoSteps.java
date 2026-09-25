package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

public class CalcularDuracionEstimadaProyectoSteps extends BaseCucumber {

    private Integer expectedTotalHours;
    private Integer projectTotalHours;

    @Before
    public void setUp() {
        super.setUp();
        expectedTotalHours = 0;
    }
    
    @Given("existe un proyecto nuevo con tareas")
    public void existeUnProyectoNuevoConTareas(DataTable table) {
        projectBuilder.createBasicProject();
        project = projectBuilder.getProject();

        List<Map<String, String>> data = table.asMaps(String.class, String.class);
        for (Map<String, String> row : data) {
            taskBuilder.createTask(
                project.getId(),
                row.get("name"),
                row.get("status"),
                row.get("ticketId"),
                row.get("estimatedHours"),
                row.get("assignedResourceId")
            );
            expectedTotalHours += Integer.parseInt(row.get("estimatedHours"));
        }
        project = projectBuilder.getProject();
        task = taskBuilder.getTask();
    }
    
    @Given("se agrega una tarea al proyecto con {int} horas estimadas")
    public void seAgregaUnaTareaAlProyectoConHorasEstimadas(Integer estimatedHours) {
        taskBuilder.createTask(
            projectBuilder.getProject().getId(),
            "Tarea Alpha",
            "IN_PROGRESS",
            "1001",
            estimatedHours.toString(),
            "123e4567-e89b-12d3-a456-426614174001"
        );
        expectedTotalHours += estimatedHours;
    }
    
    @Given("se le suman {int} horas a una tarea del proyecto")
    public void seLeSumanHorasATareaDelProyecto(Integer estimatedHours) {
        taskBuilder.modifyEstimatedHours(task.getId(), task.getEstimatedHours() + estimatedHours);
        project = projectBuilder.getProject();
        expectedTotalHours += estimatedHours;
    }

    @When("se solicita la duración estimada del proyecto")
    public void seSolicitaLaDuracionEstimadaDelProyecto() {
        projectTotalHours = projectBuilder.getProject().getEstimatedHours();
    }
    
    @Then("se muestra la duración estimada del proyecto como la suma de las horas estimadas de sus tareas")
    public void seMuestraLaDuracionEstimadaDelProyectoComoLaSumaDeLasHorasEstimadasDeSusTareas() {
        assertEquals(expectedTotalHours, projectTotalHours);
    }

}
