package com.psa.proyecto_api.repository;

import com.psa.proyecto_api.model.ProjectTag;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTagRepository extends JpaRepository<ProjectTag, Long> {
    
    // Consultas básicas por campos simples
    List<ProjectTag> findAll();
    List<ProjectTag> findByProjectId(Long projectId);
    Optional<ProjectTag> findByProjectIdAndTagName(Long projectId, String tagName);
    void deleteByProjectIdAndTagName(Long projectId, String tagName);
    List<ProjectTag> findByTagName(String tagName);
}
