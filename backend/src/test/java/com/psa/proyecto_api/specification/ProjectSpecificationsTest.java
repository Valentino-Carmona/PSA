package com.psa.proyecto_api.specification;

import com.psa.proyecto_api.dto.request.ProjectFilterRequest;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.ProjectTag;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;
import com.psa.proyecto_api.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(ProjectSpecifications.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProjectSpecificationsTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ProjectSpecifications projectSpecifications;

    private Project project1;
    private Project project2;
    private Project project3;

    @BeforeEach
    void setUp() {
        project1 = new Project();
        project1.setName("Project Alpha");
        project1.setStatus(ProjectStatus.INITIATED);
        project1.setType(ProjectType.DEVELOPMENT);
        project1.setBillingType(com.psa.proyecto_api.model.enums.ProjectBillingType.FIXED_PRICE);
        project1.setStartDate(LocalDate.now());
        
        ProjectTag tag1 = new ProjectTag();
        tag1.setTagName("backend");
        tag1.setProject(project1);
        project1.getProjectTags().add(tag1);
        
        project2 = new Project();
        project2.setName("Project Beta");
        project2.setStatus(ProjectStatus.IN_PROGRESS);
        project2.setType(ProjectType.IMPLEMENTATION);
        project2.setBillingType(com.psa.proyecto_api.model.enums.ProjectBillingType.TIME_AND_MATERIAL);
        project2.setStartDate(LocalDate.now());
        
        ProjectTag tag2 = new ProjectTag();
        tag2.setTagName("frontend");
        tag2.setProject(project2);
        project2.getProjectTags().add(tag2);
        
        project3 = new Project();
        project3.setName("Project Gamma");
        project3.setStatus(ProjectStatus.TRANSITION);
        project3.setType(ProjectType.DEVELOPMENT);
        project3.setBillingType(com.psa.proyecto_api.model.enums.ProjectBillingType.FIXED_PRICE);
        project3.setStartDate(LocalDate.now());
        // No tags

        entityManager.persist(project1);
        entityManager.persist(project2);
        entityManager.persist(project3);
        entityManager.flush();
    }

    @Test
    void testHasStatus() {
        Specification<Project> spec = projectSpecifications.hasStatus(ProjectStatus.INITIATED);
        List<Project> results = projectRepository.findAll(spec);
        
        assertEquals(1, results.size());
        assertEquals("Project Alpha", results.get(0).getName());
    }

    @Test
    void testHasType() {
        Specification<Project> spec = projectSpecifications.hasType(ProjectType.DEVELOPMENT);
        List<Project> results = projectRepository.findAll(spec);
        
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("Project Alpha")));
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("Project Gamma")));
    }

    @Test
    void testHasTag() {
        Specification<Project> spec = projectSpecifications.hasTag("backend");
        List<Project> results = projectRepository.findAll(spec);
        
        assertEquals(1, results.size());
        assertEquals("Project Alpha", results.get(0).getName());
    }

    @Test
    void testWithFilters_AllNull() {
        ProjectFilterRequest request = new ProjectFilterRequest();
        Specification<Project> spec = projectSpecifications.withFilters(request);
        List<Project> results = projectRepository.findAll(spec);
        
        assertEquals(3, results.size());
    }

    @Test
    void testWithFilters_Combined() {
        ProjectFilterRequest request = new ProjectFilterRequest();
        request.setStatus(ProjectStatus.IN_PROGRESS);
        request.setType(ProjectType.IMPLEMENTATION);
        request.setTag("frontend");
        
        Specification<Project> spec = projectSpecifications.withFilters(request);
        List<Project> results = projectRepository.findAll(spec);
        
        assertEquals(1, results.size());
        assertEquals("Project Beta", results.get(0).getName());
    }
}
