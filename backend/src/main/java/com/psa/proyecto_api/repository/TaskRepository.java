package com.psa.proyecto_api.repository;

import com.psa.proyecto_api.model.Task;
import com.psa.proyecto_api.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Consultas básicas por proyecto
    List<Task> findAll();
    List<Task> findByProjectId(Long projectId);    
    
    // Consultas por estado
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
        
    // Consultas por responsable
    List<Task> findByAssignedResourceId(String assignedResourceId);
    List<Task> findByAssignedResourceIdAndStatus(String assignedResourceId, TaskStatus status);
        
    // Consultas por etiquetas
    @Query("SELECT t FROM Task t JOIN t.taskTags tag WHERE tag.tagName = :tagName")
    List<Task> findByTagName(@Param("tagName") String tagName);
    
    @Query("SELECT t FROM Task t JOIN t.taskTags tag WHERE tag.tagName IN :tagNames")
    List<Task> findByTagNames(@Param("tagNames") List<String> tagNames);
    
    // Consulta para tareas de un proyecto con etiqueta específica
    @Query("SELECT t FROM Task t JOIN t.taskTags tag WHERE t.project.id = :projectId AND tag.tagName = :tagName")
    List<Task> findByProjectIdAndTagName(@Param("projectId") Long projectId, @Param("tagName") String tagName);
    
    // Estadísticas útiles
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    Long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedResourceId = :assignedResourceId AND t.status = :status")
    Long countByAssignedResourceIdAndStatus(@Param("assignedResourceId") String assignedResourceId, @Param("status") TaskStatus status);
    
    // Tareas sin asignar
    List<Task> findByAssignedResourceIdIsNull();
    List<Task> findByProjectIdAndAssignedResourceIdIsNull(Long projectId);

    @Query("SELECT DISTINCT t FROM Task t " +
       "WHERE t.project.id = :projectId " +
       "AND (:status IS NULL OR t.status = :status) " +
       "AND (:taskName IS NULL OR :taskName = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :taskName, '%'))) " +
       "AND (:tagName IS NULL OR :tagName = '' OR " +
       "     EXISTS (SELECT 1 FROM t.taskTags tt WHERE LOWER(tt.tagName) = LOWER(:tagName))) " +
       "AND (:ticketId IS NULL OR t.ticketId = :ticketId)")
    List<Task> findByProgressiveFilters(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("tagName") String tagName,
            @Param("taskName") String taskName,
            @Param("ticketId") Integer ticketId
    );

    @Query("SELECT DISTINCT t FROM Task t " +
       "WHERE (:ticketId IS NULL OR " +
       "        (:ticketId = -1 AND t.ticketId IS NULL) OR " +
       "        (:ticketId != -1 AND t.ticketId = :ticketId))")
List<Task> filterByTicketId(@Param("ticketId") Integer ticketId);
}