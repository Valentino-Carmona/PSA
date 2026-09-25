package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.util.List;

import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.TaskStatus;
import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class VerDetalleTareaSteps extends BaseCucumber {
    
    private boolean taskNotFound;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        taskNotFound = false;
    }

    @Given("existe una tarea detallada con los siguientes datos")
    public void existeUnaTareaDetalladaConLosSiguientesDatos(DataTable table) {
        var rows = table.asMaps(String.class, String.class);
        var row = rows.get(0);
        
        projectBuilder.createBasicProject();
        
        Long projectId = projectBuilder.getProject().getId();
        
        taskBuilder.clearTasks();
        taskBuilder.createTask(
            projectId,
            row.get("name"),
            row.get("status"),
            row.get("ticketId"),
            row.get("assignedResourceId")
        );
        
        task = taskBuilder.getTask();
        
        assertNotNull("La tarea no debería ser nula", task);
        assertEquals("El nombre de la tarea debería coincidir", row.get("name"), task.getName());
        assertEquals("El ID del proyecto debería coincidir", projectId.toString(), task.getProjectId().toString());
        assertEquals("El ID del ticket debería coincidir", row.get("ticketId"), task.getTicketId().toString());
        assertEquals("El estado de la tarea debería coincidir", row.get("status"), task.getStatus().toString());
        assertEquals("El ID del recurso asignado debería coincidir", row.get("assignedResourceId"), task.getAssignedResourceId());
    }

    @Given("no existe una tarea")
    public void noExisteUnaTarea() {
        taskBuilder.clearTasks();
        assertThrows(Exception.class, 
        () -> taskBuilder.getTask(1L));
    }

    @When("se solicita el detalle completo de la tarea")
    public void seSolicitaElDetalleCompletoDeLaTarea() {
        try {
            task = taskBuilder.getTask(1L);
            taskNotFound = false;
        } catch (Exception e) {
            taskNotFound = true;
        }
    }

    @Then("se muestra el detalle completo de la tarea")
    public void seMuestraElDetalleCompletoDeLaTarea() {
        assertNotNull("El detalle de la tarea no debería ser nulo", task);
    }

    @And("se muestran todos los atributos de la tarea")
    public void seMuestranTodosLosAtributosDeLaTarea(List<String> attributes) {
        for (String attribute : attributes) {
            switch (attribute) {
                case "id":
                    assertNotNull("El ID de la tarea no debería ser nulo", task.getId());
                    break;
                case "name":
                    assertNotNull("El nombre de la tarea no debería ser nulo", task.getName());
                    break;
                case "projectId":
                    assertNotNull("El ID del proyecto no debería ser nulo", task.getProjectId());
                    break;
                case "ticketId":
                    assertNotNull("El ID del ticket no debería ser nulo", task.getTicketId());
                    break;
                case "status":
                    assertNotNull("El estado de la tarea no debería ser nulo", task.getStatus());
                    break;
                case "estimatedHours":
                    assertNotNull("Las horas estimadas no deberían ser nulas", task.getEstimatedHours());
                    break;
                case "assignedResourceId":
                    // assignedResourceId can be null, so we don't assert it
                    break;
                case "tagNames":
                    assertNotNull("La lista de tags no debería ser nula", task.getTagNames());
                    break;
                default:
                    throw new IllegalArgumentException("Atributo no válido: " + attribute);
            }
        }
    }

    @Then("se recibe un error de tarea no encontrada")
    public void seRecibeUnErrorDeTareaNoEncontrada() {
        assertTrue("Debería indicar que la tarea no fue encontrada", taskNotFound);
    }
} 