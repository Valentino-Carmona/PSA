package com.psa.proyecto_api.service;

import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.dto.response.TaskResponse;
import com.psa.proyecto_api.dto.response.TaskSummaryResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(Long projectId, CreateTaskRequest request);
    TaskResponse updateTask(Long taskId, UpdateTaskRequest request);
    TaskResponse getTaskById(Long taskId);
    void deleteTask(Long taskId);
    TaskResponse activateTask(Long id);
    TaskResponse deactivateTask(Long id);
    List<TaskSummaryResponse> getProjectTasksFiltered(Long projectId, String status, String tag, String name, Integer ticketId);
    List<TaskSummaryResponse> getTasksByTicketId(Integer ticketId);

    TaskResponse addTagToTask(Long id, String tag);
    TaskResponse removeTagFromTask(Long id, String tag);
    TaskResponse updateTaskTag(Long id, String oldTag, String newTag);
    List<String> getTaskTags(Long id);
}
