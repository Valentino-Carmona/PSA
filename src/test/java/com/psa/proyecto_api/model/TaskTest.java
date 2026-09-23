package com.psa.proyecto_api.model;

import com.psa.proyecto_api.exception.InvalidProjectStatusException;
import com.psa.proyecto_api.exception.OperationNotAllowedException;
import com.psa.proyecto_api.exception.ResourceConflictException;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.model.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        project = new Project(
                "Mi Proyecto",
                1,
                ProjectType.DEVELOPMENT,
                ProjectBillingType.FIXED_PRICE,
                LocalDate.now()
        );
        project.setId(10L);

        task = new Task("Mi Tarea", project, 8);
        task.setId(1L);
    }

    @Test
    void constructor_ValidParameters_CreatesTaskAndLinksToProject() {
        assertEquals("Mi Tarea", task.getName());
        assertEquals(project, task.getProject());
        assertEquals(8, task.getEstimatedHours());
        assertEquals(TaskStatus.TO_DO, task.getStatus());
        assertTrue(task.getTaskTags().isEmpty());
        
        // Verifica la relacion bidireccional
        assertTrue(project.getTasks().contains(task));
    }

    @Test
    void addAssignedResource_ValidId_SetsResource() {
        task.addAssignedResource("resource-123");
        assertEquals("resource-123", task.getAssignedResourceId());
        assertTrue(task.isAssigned());
    }

    @Test
    void addAssignedResource_NullOrEmpty_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> task.addAssignedResource(null));
        assertThrows(OperationNotAllowedException.class, () -> task.addAssignedResource("   "));
    }

    @Test
    void setProject_ValidProject_SetsProject() {
        Project newProject = new Project();
        newProject.setId(20L);
        task.setProject(newProject);
        assertEquals(newProject, task.getProject());
    }

    @Test
    void setProject_NullProject_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> task.setProject(null));
    }

    @Test
    void removeFromProject_RemovesTaskFromProjectList() {
        assertTrue(project.getTasks().contains(task));
        task.removeFromProject();
        assertFalse(project.getTasks().contains(task));
    }

    @Test
    void addTag_ValidTag_AddsTag() {
        task.addTag("Backend");
        assertTrue(task.hasTag("Backend"));
    }

    @Test
    void hasTag_NullOrEmpty_ReturnsFalse() {
        assertFalse(task.hasTag(null));
        assertFalse(task.hasTag(""));
        assertFalse(task.hasTag("   "));
    }

    @Test
    void addTag_NullOrEmpty_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> task.addTag(null));
        assertThrows(OperationNotAllowedException.class, () -> task.addTag("  "));
    }

    @Test
    void addTag_DuplicateTag_ThrowsException() {
        task.addTag("Frontend");
        assertThrows(ResourceConflictException.class, () -> task.addTag("Frontend"));
    }

    @Test
    void updateDetails_ValidFields_Updates() {
        task.updateDetails("Nuevo Nombre", 10, "res-1", 100);
        assertEquals("Nuevo Nombre", task.getName());
        assertEquals(10, task.getEstimatedHours());
        assertEquals("res-1", task.getAssignedResourceId());
        assertEquals(100, task.getTicketId());
    }

    @Test
    void updateDetails_NullFields_IgnoresNulls() {
        String oldName = task.getName();
        Integer oldHours = task.getEstimatedHours();
        Integer oldTicketId = task.getTicketId();
        
        task.updateDetails(null, null, null, null);
        
        assertEquals(oldName, task.getName());
        assertEquals(oldHours, task.getEstimatedHours());
        assertEquals(oldTicketId, task.getTicketId());
        assertNull(task.getAssignedResourceId()); // assignedResourceId is set unconditionally
    }

    @Test
    void updateTaskTag_ValidTags_UpdatesSuccessfully() {
        task.addTag("Frontend");
        task.updateTaskTag("Frontend", "UI");
        
        assertFalse(task.hasTag("Frontend"));
        assertTrue(task.hasTag("UI"));
    }

    @Test
    void updateTaskTag_NullOrEmpty_ThrowsException() {
        task.addTag("Frontend");
        assertThrows(OperationNotAllowedException.class, () -> task.updateTaskTag(null, "UI"));
        assertThrows(OperationNotAllowedException.class, () -> task.updateTaskTag("Frontend", null));
        assertThrows(OperationNotAllowedException.class, () -> task.updateTaskTag("", "UI"));
        assertThrows(OperationNotAllowedException.class, () -> task.updateTaskTag("Frontend", ""));
    }

    @Test
    void updateTaskTag_DuplicateNewTag_ThrowsException() {
        task.addTag("Frontend");
        task.addTag("UI");
        assertThrows(ResourceConflictException.class, () -> task.updateTaskTag("Frontend", "UI"));
    }

    @Test
    void updateTaskTag_NonExistentOldTag_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> task.updateTaskTag("Frontend", "UI"));
    }

    @Test
    void removeTag_ValidTag_RemovesTag() {
        task.addTag("Frontend");
        task.removeTag("Frontend");
        assertFalse(task.hasTag("Frontend"));
    }

    @Test
    void removeTag_NullOrEmpty_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> task.removeTag(null));
        assertThrows(OperationNotAllowedException.class, () -> task.removeTag(" "));
    }

    @Test
    void removeTag_NonExistentTag_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> task.removeTag("Backend"));
    }

    @Test
    void statusMethods_CheckVariousStates() {
        // Inicial: TO_DO
        assertTrue(task.isToDo());
        assertFalse(task.isActive());
        assertFalse(task.isFinished());
        
        // Start: IN_PROGRESS
        task.start();
        assertFalse(task.isToDo());
        assertTrue(task.isActive());
        assertFalse(task.isFinished());
        
        // Complete: DONE
        task.complete();
        assertFalse(task.isToDo());
        assertFalse(task.isActive());
        assertTrue(task.isFinished());
    }

    @Test
    void start_WhenNotToDo_ThrowsException() {
        task.start(); // pasa a IN_PROGRESS
        assertThrows(InvalidProjectStatusException.class, () -> task.start());
    }

    @Test
    void complete_WhenNotInProgress_ThrowsException() {
        // Esta en TO_DO
        assertThrows(InvalidProjectStatusException.class, () -> task.complete());
    }

    @Test
    void startAndComplete_UpdatesProjectStatus() {
        assertEquals(ProjectStatus.INITIATED, project.getStatus());
        
        task.start();
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
        
        task.complete();
        assertEquals(ProjectStatus.TRANSITION, project.getStatus());
    }

    @Test
    void hasId_WorksCorrectly() {
        assertTrue(task.hasId(1L));
        assertFalse(task.hasId(2L));
        assertFalse(task.hasId(null));
    }

    @Test
    void getEstimatedHoursOrZero_ReturnsCorrectly() {
        assertEquals(8, task.getEstimatedHoursOrZero());
        task.setEstimatedHours(null);
        assertEquals(0, task.getEstimatedHoursOrZero());
    }

    @Test
    void isFinishedAsLong_isActiveAsLong_ReturnCorrectly() {
        assertEquals(0L, task.isActiveAsLong());
        assertEquals(0L, task.isFinishedAsLong());
        
        task.start();
        assertEquals(1L, task.isActiveAsLong());
        assertEquals(0L, task.isFinishedAsLong());
        
        task.complete();
        assertEquals(0L, task.isActiveAsLong());
        assertEquals(1L, task.isFinishedAsLong());
    }

    @Test
    void getTagNames_ReturnsSortedList() {
        task.addTag("Zeta");
        task.addTag("Alpha");
        
        List<String> tags = task.getTagNames();
        assertEquals(2, tags.size());
        assertEquals("Alpha", tags.get(0));
        assertEquals("Zeta", tags.get(1));
    }

    @Test
    void equals_And_HashCode() {
        Task t1 = new Task();
        t1.setId(1L);

        Task t2 = new Task();
        t2.setId(1L);

        Task t3 = new Task();
        t3.setId(2L);
        
        assertEquals(t1, t1); // this == o
        assertEquals(t1, t2); // same id
        assertNotEquals(t1, t3); // different id
        assertNotEquals(t1, null); // null
        assertNotEquals(t1, new Object()); // different class

        Task t4 = new Task();
        Task t5 = new Task();
        assertEquals(t4, t5);
        assertNotEquals(t1, t4);
        assertNotEquals(t4, t1);

        assertEquals(t1.hashCode(), t2.hashCode());
        assertEquals(t4.hashCode(), t5.hashCode());
    }
}
