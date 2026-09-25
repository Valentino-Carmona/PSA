package com.psa.proyecto_api.mapper;

import com.psa.proyecto_api.dto.request.CreateProjectRequest;
import com.psa.proyecto_api.dto.request.CreateTaskRequest;
import com.psa.proyecto_api.dto.request.UpdateProjectRequest;
import com.psa.proyecto_api.dto.request.UpdateTaskRequest;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.Task;
import com.psa.proyecto_api.model.enums.ProjectBillingType;
import com.psa.proyecto_api.model.enums.ProjectType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void testProjectMapperNulls() {
        ProjectMapper mapper = new ProjectMapper();
        CreateProjectRequest req = new CreateProjectRequest();
        req.setName("Test");
        req.setClientId(1);
        req.setType(ProjectType.DEVELOPMENT);
        req.setBillingType(ProjectBillingType.FIXED_PRICE);
        req.setStartDate(LocalDate.now());
        // All optional are null
        Project p = mapper.toEntity(req);
        assertNull(p.getEndDate());
        assertNull(p.getLeaderId());
        assertTrue(p.getTagNames().isEmpty());
    }

    @Test
    void testTaskMapperNulls() {
        TaskMapper mapper = new TaskMapper();
        Project p = new Project("P", 1, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        CreateTaskRequest req = new CreateTaskRequest();
        req.setName("Task");
        req.setEstimatedHours(10);
        // All optional are null
        Task t = mapper.toEntity(req, p);
        assertNull(t.getAssignedResourceId());
        assertNull(t.getTicketId());
        assertTrue(t.getTagNames().isEmpty());

        // Empty tags
        Project p2 = new Project("P2", 1, ProjectType.DEVELOPMENT, ProjectBillingType.FIXED_PRICE, LocalDate.now());
        req.setTagNames(Collections.emptyList());
        Task t2 = mapper.toEntity(req, p2);
        assertTrue(t2.getTagNames().isEmpty());
    }
}
