package com.psa.proyecto_api.controller;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.ProjectSummaryResponse;
import com.psa.proyecto_api.service.ProjectService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proyectos")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // POST /proyectos - Crear un nuevo proyecto
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PUT /proyectos/{id} - Modificar información de un proyecto
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectResponse response = projectService.updateProject(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE /proyectos/{id} - Eliminar un proyecto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // GET /proyectos?nombre=&tipo=&estado=&tag=
    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getProjects(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tag
    ) {
        List<ProjectSummaryResponse> proyectos = projectService.getProjectsFiltered(nombre, tipo, estado, tag);
        return ResponseEntity.ok(proyectos);
    }

    // GET /proyectos/{id} - Ver detalles de un proyecto específico
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    // POST /proyectos/{id}/tags - Agregar tag a un proyecto
    @PostMapping("/{id}/tags")
    public ResponseEntity<ProjectResponse> addTagToProject(
            @PathVariable Long id, 
            @RequestBody String tagName) {
        ProjectResponse response = projectService.addTagToProject(id, tagName);
        return ResponseEntity.ok(response);
    }

    // DELETE /proyectos/{id}/tags?tagName= - Quitar tag de un proyecto
    @DeleteMapping("/{id}/tags")
    public ResponseEntity<ProjectResponse> removeTagFromProject(
            @PathVariable Long id, 
            @RequestParam String tagName) {
        ProjectResponse response = projectService.removeTagFromProject(id, tagName);
        return ResponseEntity.ok(response);
    }

    // PUT /proyectos/{id}/tags?oldTagName=?newTagName= - Actualizar tag de un proyecto
    @PutMapping("/{id}/tags")
    public ResponseEntity<ProjectResponse> updateProjectTag(
            @PathVariable Long id, 
            @RequestParam String oldTagName,
            @RequestBody String newTagName) {
        ProjectResponse response = projectService.updateProjectTag(id, oldTagName, newTagName);
        return ResponseEntity.ok(response);
    }

    // GET /proyectos/{id}/tags - Obtener todos los tags de un proyecto
    @GetMapping("/{id}/tags")
    public ResponseEntity<List<String>> getProjectTags(@PathVariable Long id) {
        List<String> tags = projectService.getProjectTags(id);
        return ResponseEntity.ok(tags);
    }
}
