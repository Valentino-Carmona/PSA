package com.psa.proyecto_api.steps;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.TaskResponse;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import io.cucumber.java.en.Then;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.model.enums.TaskStatus;

@Transactional
@Rollback
public class CrearTareaSteps {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String projectApiUrl;
    private String taskApiUrl;

    private static ResponseEntity<ProjectResponse> projectResponse;
    private static ProjectResponse project;

    private CreateTaskRequest taskRequest;
    private ResponseEntity<TaskResponse> taskResponse;
    private TaskResponse task;

    private List<CreateTaskRequest> taskRequests;
    private List<TaskResponse> tasks;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        projectApiUrl = "/api/v1/proyectos";
    }

    private ProjectResponse updateProject(Long id) {
        project = restTemplate.getForEntity(
            baseUrl + projectApiUrl + "/" + id, ProjectResponse.class).getBody();
        return project;
    }

    @Given("existe el proyecto con la siguiente información:")
    public void existeElProyectoConLaSiguienteInformacion(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        var map = data.get(0);
        
        CreateProjectRequest projectRequest = new CreateProjectRequest();
        projectRequest.setName(map.get("name"));
        projectRequest.setClientId(Integer.parseInt(map.get("clientId")));
        projectRequest.setType(ProjectType.valueOf(map.get("type")));
        projectRequest.setBillingType(ProjectBillingType.valueOf(map.get("billingType")));
        projectRequest.setStartDate(LocalDate.parse(map.get("startDate")));

        projectResponse = restTemplate.postForEntity(
            baseUrl + projectApiUrl, projectRequest, ProjectResponse.class);
        project = projectResponse.getBody();
        
        if (project == null) {
            throw new RuntimeException("Project response body is null");
        }

        project = updateProject(project.getId());

        taskApiUrl = "/api/v1/proyectos/" + project.getId() + "/tareas";
    }

    @Given("se tiene la siguiente información de una tarea:")
    public void seTieneLaSiguienteInformacionDeUnaTarea(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        var map = data.get(0);

        taskRequest = new CreateTaskRequest();
        taskRequest.setName(map.get("name"));
        taskRequest.setEstimatedHours(Integer.parseInt(map.get("estimatedHours")));
    }

    @Given("se tiene la siguiente información incompleta de una tarea:")
    public void seTieneLaSiguienteInformacionIncompletaDeUnaTarea(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        var map = data.get(0);

        taskRequest = new CreateTaskRequest();
        if (map.get("name") != null) {  
            taskRequest.setName(map.get("name"));
        }
        if (map.get("estimatedHours") != null) {
            taskRequest.setEstimatedHours(Integer.parseInt(map.get("estimatedHours")));
        }
    }

    @Given("se tiene la siguiente información de tareas:")
    public void seTieneLaSiguienteInformacionDeTareas(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        taskRequests = new ArrayList<>();
        for (var map: data) {
            var request = new CreateTaskRequest();
            request.setName(map.get("name"));
            request.setEstimatedHours(Integer.parseInt(map.get("estimatedHours")));
            request.setAssignedResourceId(map.get("assignedResourceId"));
            taskRequests.add(request);
        }
    }

    @And("la tarea es para el proyecto con id {string}")
    public void laTareaEsParaElProyectoConId(String id) {
        assertEquals(Long.parseLong(id), project.getId());
    }

    @And("las tareas son para el proyecto con id {string}")
    public void lasTareasSonParaElProyectoConId(String id) {
        assertEquals(Long.parseLong(id), project.getId());
    }

    @When("el usuario intenta crear una nueva tarea")
    public void elUsuarioIntentaCrearUnaNuevaTarea() {
        try {
            taskResponse = restTemplate.postForEntity(baseUrl + taskApiUrl, taskRequest, TaskResponse.class);
            task = taskResponse.getBody();
        } catch (Exception e) {
            System.out.println("Error creating task: " + e.getMessage());
        }
    }

    @When("el usuario intenta crear las nuevas tareas")
    public void elUsuarioIntentaCrearLasNuevasTareas() {
        tasks = new ArrayList<>();
        for (var request: taskRequests) {
            try {
                taskResponse = restTemplate.postForEntity(baseUrl + taskApiUrl, request, TaskResponse.class);
                tasks.add(taskResponse.getBody());
            } catch (Exception e) {
                System.out.println("Error creating task: " + e.getMessage());
            }
        }
    }

    @Then("la tarea se crea correctamente con los siguientes datos:")
    public void laTareaSeCreaCorrectamenteConLosSiguientesDatos(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        var map = data.get(0);

        project = updateProject(project.getId());
        assertEquals(map.get("name"), task.getName());
        assertEquals(Integer.parseInt(map.get("estimatedHours")), task.getEstimatedHours());
        assertEquals(TaskStatus.valueOf(map.get("status")), task.getStatus());
        assertEquals(project.getId(), task.getProjectId());
    }

    @Then("la tarea tiene asociado el proyecto con id {string}")
    public void laTareaTieneAsociadoElProyectoConId(String projectId) {
        project = updateProject(project.getId());
        assertEquals(Long.parseLong(projectId), task.getProjectId());
    }

    @Then("el proyecto tiene una tarea")
    public void elProyectoTieneUnaTarea() {
        System.out.println(project.getTasks().stream().map(task -> task.getName()).collect(java.util.stream.Collectors.toList()));
        System.out.println(project.getTasks().size() + "=====================================================");
        project = updateProject(project.getId());
        assertEquals(1, project.getTasks().size());
    }

    @Then("el proyecto contiene la tarea {string}")
    public void elProyectoContieneLaTarea(String taskName) {
        project = updateProject(project.getId());
        assertTrue(project.getTasks().stream().anyMatch(task -> task.getName().equals(taskName)));
    }

    @Then("el proyecto contiene las tareas {string} y {string}")
    public void elProyectoContieneLasTareas(String taskName1, String taskName2) {
        project = updateProject(project.getId());
        assertTrue(project.getTasks().size() > 1);
        assertTrue(project.getTasks().stream().anyMatch(task -> task.getName().equals(taskName1)));
        assertTrue(project.getTasks().stream().anyMatch(task -> task.getName().equals(taskName2)));
    }

    @Then("las tareas se crean correctamente con los siguientes datos:")
    public void lasTareasSeCreenCorrectamenteConLosSiguientesDatos(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (int i = 0; i < data.size(); i++) {
            var map = data.get(i);
            var task = tasks.get(i);

            assertNotNull(task);
            
            assertEquals(map.get("name"), task.getName());
            assertEquals(Integer.parseInt(map.get("estimatedHours")), task.getEstimatedHours());
            assertEquals(map.get("assignedResourceId"), task.getAssignedResourceId());
            assertEquals(project.getId(), task.getProjectId());
            assertEquals(TaskStatus.valueOf(map.get("status")), task.getStatus());
        }
    }

    @Then("el proyecto tiene {int} horas estimadas")
    public void elProyectoTieneHorasEstimadas(int estimatedHours) {
        project = updateProject(project.getId());
        assertEquals(estimatedHours, project.getEstimatedHours());
    }

    @Then("se rechaza la creación de la tarea por información incompleta")
    public void elSistemaRechazaLaCreacionDeLaTareaPorInformacionIncompleta() {
        assertNull(taskResponse);
        assertNull(task);
    }
    
    @Then("se rechaza la creación de la tarea por horas negativas")
    public void elSistemaRechazaLaCreacionDeLaTareaPorHorasNegativas() {
        assertNull(taskResponse);
        assertNull(task);
    }

}