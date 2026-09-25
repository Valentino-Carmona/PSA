package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.model.enums.ProjectBillingType;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class VerDetalleProyectoSteps extends BaseCucumber {
    
    private boolean projectNotFound;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        projectNotFound = false;
    }

    @Given("existe un proyecto detallado con los siguientes datos")
    public void existeUnProyectoDetalladoConLosSiguientesDatos(DataTable table) {
        var rows = table.asMaps(String.class, String.class);
        var row = rows.get(0);
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
        
        project = projectBuilder.getProject();
        assertNotNull("El proyecto no debería ser nulo", project);
        assertEquals("El nombre del proyecto debería coincidir", row.get("name"), project.getName());
        assertEquals("El ID del cliente debería coincidir", row.get("clientId"), project.getClientId().toString());
        assertEquals("El tipo de proyecto debería coincidir", row.get("type"), project.getType().toString());
        assertEquals("El tipo de facturación debería coincidir", row.get("billingType"), project.getBillingType().toString());
        assertEquals("La fecha de inicio debería coincidir", row.get("startDate"), project.getStartDate().toString());
        assertEquals("La fecha de fin debería coincidir", row.get("endDate"), project.getEndDate().toString());
        assertEquals("El estado del proyecto debería coincidir", row.get("status"), project.getStatus().toString());
        assertEquals("El ID del líder debería coincidir", project.getLeaderId(), row.get("leaderId"));
    }

    @Given("no existen tareas en el proyecto")
    public void noExistenTareasEnElProyecto( ) {
        project = projectBuilder.getProject();
        assertTrue("La lista de tareas debería estar vacía", project.getTasks().isEmpty());
    }

    @Given("no existe un proyecto")
    public void noExisteUnProyecto() {
        projectBuilder.clearProjects();
        assertThrows(Exception.class, 
        () -> projectBuilder.getProject(1L));
    }

    @When("se solicita el detalle completo del proyecto")
    public void seSolicitaElDetalleCompletoDelProyecto() {
        try {
            project = projectBuilder.getProject(1L);
            projectNotFound = false;
        } catch (Exception e) {
            projectNotFound = true;
        }
    }

    @Then("se muestra el detalle completo del proyecto")
    public void seMuestraElDetalleCompletoDelProyecto() {
        assertNotNull("El detalle del proyecto no debería ser nulo", project);
    }

    @And("se muestran todos los atributos del proyecto")
    public void seMuestranTodosLosAtributosDelProyecto(List<String> attributes) {
        for (String attribute : attributes) {
            switch (attribute) {
                case "id":
                    assertNotNull("El ID del proyecto no debería ser nulo", project.getId());
                    break;
                case "name":
                    assertNotNull("El nombre del proyecto no debería ser nulo", project.getName());
                    break;
                case "clientId":
                    assertNotNull("El ID del cliente no debería ser nulo", project.getClientId());
                    break;
                case "type":
                    assertNotNull("El tipo del proyecto no debería ser nulo", project.getType());
                    break;
                case "billingType":
                    assertNotNull("El tipo de facturación no debería ser nulo", project.getBillingType());
                    break;
                case "startDate":
                    assertNotNull("La fecha de inicio no debería ser nula", project.getStartDate());
                    break;
                case "endDate":
                    // endDate can be null, so we don't assert it
                    break;
                case "estimatedHours":
                    assertNotNull("Las horas estimadas no deberían ser nulas", project.getEstimatedHours());
                    break;
                case "status":
                    assertNotNull("El estado del proyecto no debería ser nulo", project.getStatus());
                    break;
                case "leaderId":
                    // leaderId can be null, so we don't assert it
                    break;
                case "tagNames":
                    assertNotNull("La lista de tags no debería ser nula", project.getTagNames());
                    break;
                default:
                    throw new IllegalArgumentException("Atributo no válido: " + attribute);
            }
        }
    }

    @And("se muestran {int} tareas asociadas al proyecto")
    public void seMuestranTareasAsociadasAlProyecto(int expectedTaskCount) {
        assertNotNull("La lista de tareas no debería ser nula", project.getTasks());
        assertEquals("El número de tareas debería coincidir", expectedTaskCount, project.getTasks().size());
    }

    @And("se pueden ver las tareas con los siguientes nombres")
    public void sePuedenVerLasTareasConLosSiguientesNombres(List<String> expectedTaskNames) {
        assertNotNull("La lista de tareas no debería ser nula", project.getTasks());
        assertEquals("El número de tareas debería coincidir", expectedTaskNames.size(), project.getTasks().size());
        
        for (String expectedName : expectedTaskNames) {
            boolean found = project.getTasks().stream()
                .anyMatch(task -> expectedName.equals(task.getName()));
            assertTrue("Debería encontrar la tarea con nombre: " + expectedName, found);
        }
    }

    @Then("se recibe un error de proyecto no encontrado")
    public void seRecibeUnErrorDeProyectoNoEncontrado() {
        assertTrue("Debería indicar que el proyecto no fue encontrado", projectNotFound);
    }

    @And("existen las siguientes tareas pertenecientes al proyecto")
    public void existenLasSiguientesTareasPertenecientesAlProyecto(DataTable table) {
        var rows = table.asMaps(String.class, String.class);
        for (var row : rows) {
            taskBuilder.createTask(
                project.getId(),
                row.get("name"),
                row.get("status"),
                row.get("ticketId")
            );
        }
        project = projectBuilder.getProject();
    }
} 