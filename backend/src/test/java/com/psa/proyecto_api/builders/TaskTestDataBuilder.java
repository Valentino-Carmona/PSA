package com.psa.proyecto_api.builders;

import java.util.List;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;
import com.psa.proyecto_api.model.enums.TaskStatus;

/**
 * Builder class for creating and managing tasks in tests.
 * Provides methods to create, update, and manipulate tasks for testing purposes.
 */
public class TaskTestDataBuilder {

    // ========================================================================
    // CONSTANTS
    // ========================================================================
    
    private static final String DEFAULT_TASK_NAME = "Tarea de Prueba";
    private static final Integer DEFAULT_ESTIMATED_HOURS = 1;
    private static final Long DEFAULT_RESOURCE_ID = 1L;
    private static final String ACTIVATE_ENDPOINT = "/activar";
    private static final String DEACTIVATE_ENDPOINT = "/desactivar";
    private static final String TASKS_ENDPOINT = "/tareas";
    private static final String PROJECTS_ENDPOINT = "/proyectos";

    // ========================================================================
    // INSTANCE FIELDS
    // ========================================================================
    
    private final String baseUrl;
    private final TestRestTemplate restTemplate;
    private ResponseEntity<TaskResponse> response;
    private TaskResponse task;

    // ========================================================================
    // CONSTRUCTOR
    // ========================================================================
    
    /**
     * Constructor for TaskTestDataBuilder
     * @param baseUrl The base URL for API requests
     * @param restTemplate The REST template for making HTTP requests
     */
    public TaskTestDataBuilder(String baseUrl, TestRestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
        this.response = null;
        this.task = null;
    }

    // ========================================================================
    // PRIVATE HELPER METHODS
    // ========================================================================
    
    /**
     * Sets minimal default values for a task request
     * @param request The task request to populate
     * @return The populated task request with minimal defaults
     */
    private CreateTaskRequest setMinimalDefaults(CreateTaskRequest request) {
        request.setName(DEFAULT_TASK_NAME);
        request.setEstimatedHours(DEFAULT_ESTIMATED_HOURS);
        return request;
    }

    /**
     * Constructs the full URL for an API endpoint
     * @param endpoint The endpoint path to append to the base URL
     * @return The complete URL
     */
    private String url(String endpoint) {
        return baseUrl + endpoint;
    }

    // ========================================================================
    // PUBLIC TASK STATUS METHODS
    // ========================================================================
    
    /**
     * Changes the status of the current task
     * @param status The new status to set
     */
    public void changeStatus(String status) {
        updateTask();
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        if (status.equals(task.getStatus().toString())) {
            return;
        }
        TaskStatus newStatus = TaskStatus.valueOf(status);
        try {
            switch (newStatus) {
                case IN_PROGRESS:
                    activate(task);
                    break;
                case DONE:
                    if (task.getStatus() != TaskStatus.IN_PROGRESS) {
                        activate(task);
                    }
                    deactivate(task);
                    break;
                default:
                    throw new RuntimeException("Invalid status: " + status);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to change task status to '" + status + "'. " + e.getMessage(), e);
        }
    }

    /**
     * Activates a task, changing its status to IN_PROGRESS
     * @param task The task to activate
     */
    public void activate(TaskResponse task) {
        String url = url(TASKS_ENDPOINT + "/" + task.getId() + ACTIVATE_ENDPOINT);
        try {
            response = restTemplate.exchange(
                url, HttpMethod.PUT, null, TaskResponse.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Task activation failed with status: " + response.getStatusCode());
            }
            this.task = response.getBody();
            if (this.task == null) {
                throw new RuntimeException("Task activation returned null");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to activate task. The API likely returned an error response. " +
                "Original error: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a task, changing its status to DONE
     * @param task The task to deactivate
     */
    public void deactivate(TaskResponse task) {
        String url = url(TASKS_ENDPOINT + "/" + task.getId() + DEACTIVATE_ENDPOINT);
        try {
            response = restTemplate.exchange(
                url, HttpMethod.PUT, null, TaskResponse.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Task deactivation failed with status: " + response.getStatusCode());
            }
            
            this.task = response.getBody();
            if (this.task == null) {
                throw new RuntimeException("Task deactivation returned null");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error deactivating task", e);
        }
    }

    // ========================================================================
    // PUBLIC CREATION METHODS
    // ========================================================================
    
    /**
     * Creates a basic task with minimal default values
     * @param projectId The ID of the project to create the task for
     */
    public void createBasicTask(Long projectId) {
        CreateTaskRequest request = new CreateTaskRequest();
        request = setMinimalDefaults(request);
        
        try {
            response = restTemplate.postForEntity(
                url(PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT), request, TaskResponse.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create task. HTTP Status: " + response.getStatusCode() + 
                    ", Body: " + response.getBody());
            }
            
            if (response.getBody() == null) {
                throw new RuntimeException("Task response body is null");
            }
            
            task = response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task. The API likely returned an error response. " +
                "Original error: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a basic task and sets it to the specified status
     * @param projectId The ID of the project to create the task for
     * @param status The status to set for the task
     */
    public void createBasicTaskWithStatus(Long projectId, String status) {
        createBasicTask(projectId);
        changeStatus(status);
    }

    /**
     * Creates a task with a specific name and status
     * @param name The name for the task
     * @param projectId The ID of the project to create the task for
     * @param status The status to set for the task
     */
    public void createTaskWithStatus(String name, Long projectId, String status) {
        CreateTaskRequest request = new CreateTaskRequest();
        request = setMinimalDefaults(request);
        request.setName(name);
        response = restTemplate.postForEntity(url(
            PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT), request, TaskResponse.class);
        this.task = response.getBody();
        System.out.println("Task created: " + this.task);
        changeStatus(status);
    }

    public void createTask(Long projectId, String name, String status, String ticketId) {
        CreateTaskRequest request = new CreateTaskRequest();
        request = setMinimalDefaults(request);
        request.setName(name);
        request.setTicketId(Integer.parseInt(ticketId));
        try {
            response = restTemplate.postForEntity(
                url(PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT),
                request, TaskResponse.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create task. HTTP Status: " + response.getStatusCode() + 
                    ", Body: " + response.getBody());
            }
            if (response.getBody() == null) {
                throw new RuntimeException("Task response body is null");
            }
            task = response.getBody();
            changeStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task. The API likely returned an error response. " +
                "Original error: " + e.getMessage(), e);
        }
    }

    public void createTask(Long projectId, String name, String status, String ticketId, String assignedResourceId) {
        CreateTaskRequest request = new CreateTaskRequest();
        request = setMinimalDefaults(request);
        request.setName(name);
        request.setTicketId(Integer.parseInt(ticketId));
        request.setAssignedResourceId(assignedResourceId);
        try {
            response = restTemplate.postForEntity(
                url(PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT), request, TaskResponse.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create task. HTTP Status: " + response.getStatusCode() + 
                    ", Body: " + response.getBody());
            }
            if (response.getBody() == null) {
                throw new RuntimeException("Task response body is null");
            }
            this.task = response.getBody();
            changeStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task. The API likely returned an error response. " +
                "Original error: " + e.getMessage(), e);
        }
    }

    public void createTask(Long projectId, String name, String status, String ticketId, String estimatedHours, String assignedResourceId) {
        CreateTaskRequest request = new CreateTaskRequest();
        request = setMinimalDefaults(request);
        request.setName(name);
        request.setTicketId(Integer.parseInt(ticketId));
        request.setAssignedResourceId(assignedResourceId);
        request.setEstimatedHours(Integer.parseInt(estimatedHours));
        try {
            response = restTemplate.postForEntity(
                url(PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT), request, TaskResponse.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create task. HTTP Status: " + response.getStatusCode() + 
                    ", Body: " + response.getBody());
            }
            if (response.getBody() == null) {
                throw new RuntimeException("Task response body is null");
            }
            this.task = response.getBody();
            changeStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task. The API likely returned an error response. " +
                "Original error: " + e.getMessage(), e);
        }
    }

    public void createTaskWithAssignedResource(Long projectId, String resourceId) {
        CreateTaskRequest request = new CreateTaskRequest();
        request = setMinimalDefaults(request);
        request.setAssignedResourceId(resourceId);
        response = restTemplate.postForEntity(
            url(PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT), request, TaskResponse.class);
        this.task = response.getBody();
        System.out.println("Task created: " + this.task);
    }

    // ========================================================================
    // PUBLIC GETTER METHODS
    // ========================================================================
    
    /**
     * Gets the current task after updating it from the server
     * @return The current task response
     */
    public TaskResponse getTask() {
        updateTask();
        return task;
    }

    public TaskResponse getTask(Long id) {
        try {
            String url = url(TASKS_ENDPOINT + "/" + id);
            response = restTemplate.getForEntity(url, TaskResponse.class);
            
            // Check if the response is successful before trying to deserialize
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get task: " + id + ". Status: " + response.getStatusCode());
            }
            
            task = response.getBody();
            return task;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get task: " + id + ". Error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the current task data from the server
     */
    public void updateTask() {
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        String url = url(TASKS_ENDPOINT + "/" + task.getId());
        try {
            response = restTemplate.getForEntity(url, TaskResponse.class);
            
            // Check if the response is successful before trying to deserialize
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to update task. Status: " + response.getStatusCode());
            }
            
            task = response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update task. Error: " + e.getMessage(), e);
        }
    }

    public List<TaskResponse> getTasksBy(Long projectId, String filter) {
        String url = url(PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT + filter);
        ResponseEntity<List<TaskResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<TaskResponse>>() {});
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get tasks. Status: " + response.getStatusCode());
        }

        return response.getBody();
    }

    public List<TaskSummaryResponse> getTasksSummaryBy(Long projectId, String filter) {
        String url = url(PROJECTS_ENDPOINT + "/" + projectId + TASKS_ENDPOINT + filter);
        ResponseEntity<List<TaskSummaryResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<TaskSummaryResponse>>() {});
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get tasks. Status: " + response.getStatusCode());
        }

        return response.getBody();
    }

    // ========================================================================
    // PUBLIC DELETE METHODS
    // ========================================================================

    public void clearTasks() {
        // Get all tasks (assuming an endpoint exists to list all tasks)
        String url = baseUrl + "/tareas";
        ResponseEntity<List<TaskResponse>> response = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<List<TaskResponse>>() {});
        List<TaskResponse> tasks = response.getBody();
        if (tasks != null) {
            for (TaskResponse task : tasks) {
                restTemplate.exchange(
                    baseUrl + "/tareas/" + task.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
                );
            }
        }
    }

    // ========================================================================
    // PUBLIC MODIFICATION METHODS
    // ========================================================================

    public void assignResourceToTask(Long taskId, String resourceId) {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setAssignedResourceId(resourceId);
        String url = url(TASKS_ENDPOINT + "/" + taskId);
        try {
            response = restTemplate.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(request), TaskResponse.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to assign resource to task. Status: " + response.getStatusCode());
            }
            task = response.getBody(); 
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign resource to task. Error: " + e.getMessage(), e);
        }
    }

    public void modifyEstimatedHours(Long taskId, int estimatedHours) {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setEstimatedHours(estimatedHours);
        String url = url(TASKS_ENDPOINT + "/" + taskId);
        try {
            response = restTemplate.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(request), TaskResponse.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to modify estimated hours. Status: " + response.getStatusCode());
            }
            task = response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to modify estimated hours. Error: " + e.getMessage(), e);
        }
    }
}
