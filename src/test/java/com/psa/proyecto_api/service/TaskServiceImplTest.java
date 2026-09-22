package com.psa.proyecto_api.service;

import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;
import com.psa.proyecto_api.exception.ProjectNotFoundException;
import com.psa.proyecto_api.exception.TaskNotFoundException;
import com.psa.proyecto_api.mapper.TaskMapper;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.Task;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.model.enums.TaskStatus;
import com.psa.proyecto_api.repository.ProjectRepository;
import com.psa.proyecto_api.repository.TaskRepository;
import com.psa.proyecto_api.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Project project;
    private Task task;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStatus(ProjectStatus.INITIATED);
        project.setType(ProjectType.DEVELOPMENT);
        project.setBillingType(ProjectBillingType.FIXED_PRICE);
        project.setStartDate(LocalDate.now());

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setStatus(TaskStatus.TO_DO);
        task.setProject(project);

        taskResponse = TaskResponse.builder()
                .id(1L)
                .name("Test Task")
                .build();
    }

    @Test
    void createTask_WhenProjectExists_ReturnsTaskResponse() {
        CreateTaskRequest request = new CreateTaskRequest();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskMapper.toEntity(request, project)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.createTask(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(taskRepository).save(task);
    }

    @Test
    void createTask_WhenProjectNotFound_ThrowsProjectNotFoundException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> taskService.createTask(99L, new CreateTaskRequest()));
    }

    @Test
    void updateTask_WhenExists_ReturnsUpdatedResponse() {
        UpdateTaskRequest request = new UpdateTaskRequest();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(1L, request);

        assertNotNull(result);
        verify(taskMapper).updateEntity(task, request);
    }

    @Test
    void updateTask_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(99L, new UpdateTaskRequest()));
    }

    @Test
    void activateTask_WhenExists_StartsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.activateTask(1L);

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void activateTask_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.activateTask(99L));
    }

    @Test
    void deactivateTask_WhenExists_CompletesTask() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.deactivateTask(1L);

        assertNotNull(result);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void deactivateTask_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deactivateTask(99L));
    }

    @Test
    void getTaskById_WhenExists_ReturnsResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTaskById_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void deleteTask_WhenExists_DeletesSuccessfully() {
        // Need to properly link task to project's task list so removeFromProject works
        project.getTasks().add(task);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
        assertFalse(project.getTasks().contains(task));
    }

    @Test
    void deleteTask_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void addTagToTask_WhenExists_ReturnsUpdatedTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.addTagToTask(1L, "urgent");

        assertNotNull(result);
        verify(taskRepository).save(task);
    }

    @Test
    void addTagToTask_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.addTagToTask(99L, "urgent"));
    }

    @Test
    void removeTagFromTask_WhenExists_ReturnsUpdatedTask() {
        task.addTag("urgent");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.removeTagFromTask(1L, "urgent");

        assertNotNull(result);
    }

    @Test
    void removeTagFromTask_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.removeTagFromTask(99L, "urgent"));
    }

    @Test
    void updateTaskTag_WhenExists_ReturnsUpdatedTask() {
        task.addTag("old");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTaskTag(1L, "old", "new");

        assertNotNull(result);
    }

    @Test
    void updateTaskTag_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskTag(99L, "old", "new"));
    }

    @Test
    void getTaskTags_WhenExists_ReturnsTags() {
        task.addTag("urgent");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        List<String> tags = taskService.getTaskTags(1L);

        assertNotNull(tags);
        assertTrue(tags.contains("urgent"));
    }

    @Test
    void getTaskTags_WhenNotFound_ThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskTags(99L));
    }

    @Test
    void getTasksByTicketId_ReturnsMatchingTasks() {
        TaskSummaryResponse summary = TaskSummaryResponse.builder().id(1L).name("Test Task").build();
        when(taskRepository.filterByTicketId(1001)).thenReturn(List.of(task));
        when(taskMapper.toSummary(task)).thenReturn(summary);

        List<TaskSummaryResponse> results = taskService.getTasksByTicketId(1001);

        assertEquals(1, results.size());
    }

    @Test
    void getProjectTasksFiltered_WithNullStatus_ReturnsAll() {
        TaskSummaryResponse summary = TaskSummaryResponse.builder().id(1L).name("Test Task").build();
        when(taskRepository.findByProgressiveFilters(1L, null, null, null, null)).thenReturn(List.of(task));
        when(taskMapper.toSummary(task)).thenReturn(summary);

        List<TaskSummaryResponse> results = taskService.getProjectTasksFiltered(1L, null, null, null, null);

        assertEquals(1, results.size());
    }
}
