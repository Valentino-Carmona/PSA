package com.psa.proyecto_api.controller;

import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;
import com.psa.proyecto_api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // POST /proyectos/{id}/tareas - Crear una tarea dentro de un proyecto
    @PostMapping("/proyectos/{projectId}/tareas")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(projectId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PUT /tareas/{id} - Modificar una tarea
    @PutMapping("/tareas/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    // PUT /tareas/{id}/activar - Activar una tarea
    @PutMapping("/tareas/{id}/activar")
    public ResponseEntity<TaskResponse> activateTask(@PathVariable Long id) {
        TaskResponse response = taskService.activateTask(id);
        return ResponseEntity.ok(response);
    }

    // PUT /tareas/{id}/desactivar - Desactivar una tarea (cambiar estado a TO_DO)
    @PutMapping("/tareas/{id}/desactivar")
    public ResponseEntity<TaskResponse> deactivateTask(@PathVariable Long id) {
        TaskResponse response = taskService.deactivateTask(id);
        return ResponseEntity.ok(response);
    }

    // GET /proyectos/{id}/tareas?estado=&etiqueta=&nombre=&ticketId=
    @GetMapping("/proyectos/{id}/tareas")
    public ResponseEntity<List<TaskSummaryResponse>> getProjectTasks(
            @PathVariable Long id,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String etiqueta,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer ticketId
    ) {
        List<TaskSummaryResponse> tareas = taskService.getProjectTasksFiltered(id, estado, etiqueta, nombre, ticketId);
        return ResponseEntity.ok(tareas);
    }

    // GET /tareas?ticketId=
    @GetMapping("/tareas")
    public ResponseEntity<List<TaskSummaryResponse>> getTasksByTicketId(
            @RequestParam(required = false) Integer ticketId) {
        List<TaskSummaryResponse> tareas = taskService.getTasksByTicketId(ticketId);
        return ResponseEntity.ok(tareas);
    }

    // GET /tareas/{id} - Obtener detalles de una tarea específica
    @GetMapping("/tareas/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    // DELETE /tareas/{id} - Eliminar una tarea
    @DeleteMapping("/tareas/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // POST /tareas/{id}/tags - Agregar tag a una tarea
    @PostMapping("/tareas/{id}/tags")
    public ResponseEntity<TaskResponse> addTagToTask(
            @PathVariable Long id,
            @RequestBody String tagName) {
        TaskResponse response = taskService.addTagToTask(id, tagName);
        return ResponseEntity.ok(response);
    }

    // DELETE /tareas/{id}/tags - Quitar tag de una tarea
    @DeleteMapping("/tareas/{id}/tags")
    public ResponseEntity<TaskResponse> removeTagFromTask(
            @PathVariable Long id,
            @RequestParam String tagName) {
        TaskResponse response = taskService.removeTagFromTask(id, tagName);
        return ResponseEntity.ok(response);
    }

    // PUT /tareas/{id}/tags - Actualizar tag de una tarea
    @PutMapping("/tareas/{id}/tags")
    public ResponseEntity<TaskResponse> updateTaskTag(
            @PathVariable Long id,
            @RequestParam String oldTagName,
            @RequestBody String newTagName) {
        TaskResponse response = taskService.updateTaskTag(id, oldTagName, newTagName);
        return ResponseEntity.ok(response);
    }

    // GET /tareas/{id}/tags - Obtener todos los tags de una tarea
    @GetMapping("/tareas/{id}/tags")
    public ResponseEntity<List<String>> getTaskTags(@PathVariable Long id) {
        List<String> tags = taskService.getTaskTags(id);
        return ResponseEntity.ok(tags);
    }
}