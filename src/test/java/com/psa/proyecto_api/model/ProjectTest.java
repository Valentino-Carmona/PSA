package com.psa.proyecto_api.model;

import com.psa.proyecto_api.exception.OperationNotAllowedException;
import com.psa.proyecto_api.exception.ResourceConflictException;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project(
                "Mi Proyecto",
                1,
                ProjectType.DEVELOPMENT,
                ProjectBillingType.FIXED_PRICE,
                LocalDate.now()
        );
    }

    @Test
    void constructor_ValidParameters_CreatesProject() {
        assertEquals("Mi Proyecto", project.getName());
        assertEquals(1, project.getClientId());
        assertEquals(ProjectType.DEVELOPMENT, project.getType());
        assertEquals(ProjectBillingType.FIXED_PRICE, project.getBillingType());
        assertNotNull(project.getStartDate());
        assertEquals(ProjectStatus.INITIATED, project.getStatus());
        assertEquals(0, project.getEstimatedHours());
        assertTrue(project.getTasks().isEmpty());
        assertTrue(project.getProjectTags().isEmpty());
    }

    @Test
    void constructor_NullClientId_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> 
            new Project("Test", null, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now())
        );
    }

    @Test
    void addEndDate_ValidDate_SetsDate() {
        LocalDate end = LocalDate.now().plusDays(5);
        project.addEndDate(end);
        assertEquals(end, project.getEndDate());
    }

    @Test
    void addEndDate_BeforeStartDate_IgnoresDate() {
        LocalDate end = LocalDate.now().minusDays(5);
        project.addEndDate(end);
        assertNull(project.getEndDate());
    }

    @Test
    void addLeader_ValidId_SetsLeader() {
        project.addLeader("12345678-1234-1234-1234-123456789012");
        assertEquals("12345678-1234-1234-1234-123456789012", project.getLeaderId());
    }

    @Test
    void addLeader_EmptyOrNull_Ignores() {
        project.addLeader("");
        assertNull(project.getLeaderId());
        
        project.addLeader(null);
        assertNull(project.getLeaderId());
    }

    @Test
    void addTask_ValidTask_AddsToProject() {
        Task task = new Task();
        task.setId(1L);
        project.addTask(task);
        
        assertTrue(project.getTasks().contains(task));
    }

    @Test
    void addTask_NullTask_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> project.addTask(null));
    }

    @Test
    void addTask_DuplicateTask_ThrowsException() {
        Task task = new Task();
        task.setId(1L);
        project.addTask(task);
        
        assertThrows(ResourceConflictException.class, () -> project.addTask(task));
    }

    @Test
    void removeTask_ValidTask_RemovesFromProject() {
        Task task = new Task();
        task.setId(1L);
        project.addTask(task);
        assertTrue(project.getTasks().contains(task));
        
        project.removeTask(task);
        assertFalse(project.getTasks().contains(task));
    }

    @Test
    void removeTask_NullTask_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> project.removeTask(null));
    }

    @Test
    void removeTask_NonExistentTask_ThrowsException() {
        Task task = new Task();
        task.setId(1L);
        assertThrows(OperationNotAllowedException.class, () -> project.removeTask(task));
    }

    @Test
    void addTag_ValidTag_AddsTag() {
        project.addTag("Backend");
        assertTrue(project.hasTag("Backend"));
    }

    @Test
    void hasTag_NullOrEmpty_ReturnsFalse() {
        assertFalse(project.hasTag(null));
        assertFalse(project.hasTag(""));
        assertFalse(project.hasTag("   "));
    }

    @Test
    void addTag_NullOrEmpty_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> project.addTag(null));
        assertThrows(OperationNotAllowedException.class, () -> project.addTag(" "));
    }

    @Test
    void addTag_DuplicateTag_ThrowsException() {
        project.addTag("Backend");
        assertThrows(ResourceConflictException.class, () -> project.addTag("Backend"));
    }

    @Test
    void removeTag_ValidTag_RemovesTag() {
        project.addTag("Backend");
        project.removeTag("Backend");
        assertFalse(project.hasTag("Backend"));
    }

    @Test
    void removeTag_NullOrEmpty_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> project.removeTag(null));
        assertThrows(OperationNotAllowedException.class, () -> project.removeTag(" "));
    }

    @Test
    void removeTag_NonExistentTag_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> project.removeTag("Frontend"));
    }

    @Test
    void updateProjectTag_ValidTags_UpdatesSuccessfully() {
        project.addTag("Backend");
        project.updateProjectTag("Backend", "API");
        
        assertFalse(project.hasTag("Backend"));
        assertTrue(project.hasTag("API"));
    }

    @Test
    void updateProjectTag_NullOrEmpty_ThrowsException() {
        project.addTag("Backend");
        assertThrows(OperationNotAllowedException.class, () -> project.updateProjectTag(null, "API"));
        assertThrows(OperationNotAllowedException.class, () -> project.updateProjectTag("Backend", null));
        assertThrows(OperationNotAllowedException.class, () -> project.updateProjectTag("", "API"));
        assertThrows(OperationNotAllowedException.class, () -> project.updateProjectTag("Backend", ""));
    }

    @Test
    void updateProjectTag_DuplicateNewTag_ThrowsException() {
        project.addTag("Backend");
        project.addTag("API");
        assertThrows(ResourceConflictException.class, () -> project.updateProjectTag("Backend", "API"));
    }

    @Test
    void updateProjectTag_NonExistentOldTag_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> project.updateProjectTag("Backend", "API"));
    }

    @Test
    void getTagNames_ReturnsSortedList() {
        project.addTag("Zeta");
        project.addTag("Alpha");
        project.addTag("Beta");
        
        List<String> names = project.getTagNames();
        assertEquals(3, names.size());
        assertEquals("Alpha", names.get(0));
        assertEquals("Beta", names.get(1));
        assertEquals("Zeta", names.get(2));
    }

    @Test
    void updateDetails_ValidFields_Updates() {
        project.updateDetails("Nuevo Nombre", 2, "new-leader");
        assertEquals("Nuevo Nombre", project.getName());
        assertEquals("new-leader", project.getLeaderId());
    }

    @Test
    void updateTypes_ValidFields_Updates() {
        project.updateTypes(ProjectType.IMPLEMENTATION, ProjectBillingType.TIME_AND_MATERIAL);
        assertEquals(ProjectType.IMPLEMENTATION, project.getType());
        assertEquals(ProjectBillingType.TIME_AND_MATERIAL, project.getBillingType());
    }

    @Test
    void updateDates_ValidDates_Updates() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(10);
        project.updateDates(start, end);
        assertEquals(start, project.getStartDate());
        assertEquals(end, project.getEndDate());
    }

    @Test
    void updateDetails_NullFields_IgnoresNulls() {
        String oldName = project.getName();
        Integer oldClientId = project.getClientId();
        String oldLeader = project.getLeaderId();
        
        project.updateDetails(null, null, null);
        
        assertEquals(oldName, project.getName());
        assertEquals(oldClientId, project.getClientId());
        // leaderId might be null, but we test that it didn't crash
    }

    @Test
    void updateTypes_NullFields_IgnoresNulls() {
        ProjectType oldType = project.getType();
        ProjectBillingType oldBilling = project.getBillingType();
        
        project.updateTypes(null, null);
        
        assertEquals(oldType, project.getType());
        assertEquals(oldBilling, project.getBillingType());
    }

    @Test
    void updateDates_NullDates_IgnoresNulls() {
        LocalDate oldStart = project.getStartDate();
        LocalDate oldEnd = project.getEndDate();
        
        project.updateDates(null, null);
        
        assertEquals(oldStart, project.getStartDate());
        assertEquals(oldEnd, project.getEndDate());
    }

    @Test
    void statusSwitch_NoTasks_Initiated() {
        project.statusSwitch();
        assertEquals(ProjectStatus.INITIATED, project.getStatus());
        assertTrue(project.isInitiated());
        assertFalse(project.isActive());
        assertFalse(project.isFinished());
    }

    @Test
    void statusSwitch_ActiveTask_InProgress() {
        Task task = new Task();
        task.setId(1L);
        task.setProject(project);
        project.addTask(task);
        task.start();
        
        project.statusSwitch();
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
        assertTrue(project.isActive());
    }

    @Test
    void statusSwitch_AllTasksFinished_Transition() {
        Task task = new Task();
        task.setId(1L);
        task.setProject(project);
        project.addTask(task);
        task.start();
        task.complete();
        
        project.statusSwitch();
        assertEquals(ProjectStatus.TRANSITION, project.getStatus());
        assertTrue(project.isFinished());
    }

    @Test
    void getTotalEstimatedHoursFromTasks_CalculatesCorrectly() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setEstimatedHours(10);
        
        Task t2 = new Task();
        t2.setId(2L);
        t2.setEstimatedHours(5);
        
        project.addTask(t1);
        project.addTask(t2);
        
        assertEquals(15, project.getTotalEstimatedHoursFromTasks());
        assertEquals(15, project.getEstimatedHours());
    }

    @Test
    void getProgressPercentage_EmptyTasks_ReturnsZero() {
        assertEquals(0.0, project.getProgressPercentage());
    }

    @Test
    void getProgressPercentage_WithTasks_CalculatesCorrectly() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setProject(project);
        project.addTask(t1);
        t1.start();
        t1.complete();
        
        Task t2 = new Task();
        t2.setId(2L);
        t2.setProject(project);
        project.addTask(t2);
        t2.start();
        
        assertEquals(50.0, project.getProgressPercentage());
        assertEquals(1, project.getActiveTasksCount());
        assertEquals(1, project.getCompletedTasksCount());
    }

    @Test
    void getUnassignedTasks_FiltersCorrectly() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.addAssignedResource("res-1");
        
        Task t2 = new Task();
        t2.setId(2L);
        
        project.addTask(t1);
        project.addTask(t2);
        
        List<Task> unassigned = project.getUnassignedTasks();
        assertEquals(1, unassigned.size());
        assertEquals(2L, unassigned.get(0).getId());
    }

    @Test
    void equals_And_HashCode() {
        Project p1 = new Project();
        p1.setId(1L);

        Project p2 = new Project();
        p2.setId(1L);

        Project p3 = new Project();
        p3.setId(2L);
        
        assertEquals(p1, p1);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, new Object());

        Project p4 = new Project(); // null id
        Project p5 = new Project(); // null id
        assertEquals(p4, p5); // both null ids
        assertNotEquals(p1, p4); // this id non-null, other null
        assertNotEquals(p4, p1); // this id null, other non-null

        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(p4.hashCode(), p5.hashCode());
    }

    @Test
    void findTaskById_ExistingId_ReturnsTask() {
        Task t1 = new Task();
        t1.setId(5L);
        project.addTask(t1);
        
        Optional<Task> found = project.findTaskById(5L);
        assertTrue(found.isPresent());
        assertEquals(5L, found.get().getId());
    }
    
    @Test
    void findTaskById_NonExistingId_ReturnsEmpty() {
        Optional<Task> found = project.findTaskById(99L);
        assertFalse(found.isPresent());
    }

    @Test
    void findTaskById_NullId_ReturnsEmpty() {
        Optional<Task> found = project.findTaskById(null);
        assertFalse(found.isPresent());
    }

    @Test
    void isValidDateRange_ValidationsWork() throws Exception {
        Method method = Project.class.getDeclaredMethod("isValidDateRange");
        method.setAccessible(true);
        
        // Valid dates
        project.updateDates(LocalDate.now(), LocalDate.now().plusDays(5));
        assertTrue((Boolean) method.invoke(project));

        // Null endDate is valid
        project.updateDates(LocalDate.now(), null);
        assertTrue((Boolean) method.invoke(project));

        // Equal dates is valid
        project.updateDates(LocalDate.now(), LocalDate.now());
        assertTrue((Boolean) method.invoke(project));

        // Invalid dates (end before start)
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().minusDays(1));
        assertFalse((Boolean) method.invoke(project));
    }
}