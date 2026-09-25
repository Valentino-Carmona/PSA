package com.psa.proyecto_api.service.impl;

import com.psa.proyecto_api.service.TaskService;
import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;
import com.psa.proyecto_api.exception.ProjectNotFoundException;
import com.psa.proyecto_api.exception.TaskNotFoundException;
import com.psa.proyecto_api.mapper.TaskMapper;
import com.psa.proyecto_api.model.Task;
import com.psa.proyecto_api.model.enums.TaskStatus;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.repository.TaskRepository;
import com.psa.proyecto_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    // Task Methods

    @Override
    public TaskResponse createTask(Long projectId, CreateTaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        Task task = taskMapper.toEntity(request, project);
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        taskMapper.updateEntity(task, request);
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }   

    @Override
    public TaskResponse activateTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.start();

        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }
    
    @Override
    public TaskResponse deactivateTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.complete();

        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        return taskMapper.toResponse(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.removeFromProject();
        taskRepository.deleteById(taskId);
    }

    @Override
    public List<TaskSummaryResponse> getProjectTasksFiltered(Long projectId, String status, String tag, String name, Integer ticketId) {
        TaskStatus taskStatus = TaskStatus.fromString(status);

        List<Task> tasks = taskRepository.findByProgressiveFilters(projectId, taskStatus, tag, name, ticketId);
        return tasks.stream()
                .map(taskMapper::toSummary)
                .toList();
    }

    // TaskTag Methods

    @Override
    public TaskResponse addTagToTask(Long id, String tag) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.addTag(tag);
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse removeTagFromTask(Long id, String tag) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.removeTag(tag);
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTaskTag(Long id, String oldTag, String newTag) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.updateTaskTag(oldTag, newTag);
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Override
    public List<String> getTaskTags(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return task.getTagNames();
    }

    @Override
    public List<TaskSummaryResponse> getTasksByTicketId(Integer ticketId) {
        List<Task> tasks = taskRepository.filterByTicketId(ticketId);
            
        return tasks.stream()
               .map(taskMapper::toSummary)
               .toList();
    }
}
