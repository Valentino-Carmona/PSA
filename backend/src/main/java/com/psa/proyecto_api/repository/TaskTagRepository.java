package com.psa.proyecto_api.repository;

import com.psa.proyecto_api.model.TaskTag;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTag, Long> {
    
    // Consultas básicas por campos simples
    List<TaskTag> findAll();
    List<TaskTag> findByTaskId(Long taskId);
    Optional<TaskTag> findByTaskIdAndTagName(Long taskId, String tagName);
    void deleteByTaskIdAndTagName(Long taskId, String tagName);
    List<TaskTag> findByTagName(String tagName);
}
