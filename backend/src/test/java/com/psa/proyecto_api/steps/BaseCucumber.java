package com.psa.proyecto_api.steps;

import static org.junit.Assert.*;

import com.psa.proyecto_api.BaseIntegrationTest;
import com.psa.proyecto_api.builders.ProjectTestDataBuilder;
import com.psa.proyecto_api.builders.TaskTestDataBuilder;
import com.psa.proyecto_api.dto.response.ProjectResponse;
import com.psa.proyecto_api.dto.response.TaskResponse;

import io.cucumber.java.Before;

/**
 * Base class for all Cucumber step definitions.
 * 
 * This class provides:
 * - Common setup and shared resources for test steps
 * - Access to test data builders for projects and tasks
 * - Shared response objects for assertions
 * 
 * All step definition classes should extend this class to inherit
 * the common functionality and maintain consistency across tests.
 */
public class BaseCucumber extends BaseIntegrationTest {
    
    // Test data builders for creating test entities
    protected ProjectTestDataBuilder projectBuilder;
    protected TaskTestDataBuilder taskBuilder;

    // Shared response objects for assertions
    protected ProjectResponse project;
    protected TaskResponse task;

    /**
     * Sets up the test environment by initializing test data builders.
     * This method is called before each test scenario.
     */
    @Override
    public void setUp() {
        super.setUp();
        taskBuilder = new TaskTestDataBuilder(getBaseUrl(), restTemplate);
        projectBuilder = new ProjectTestDataBuilder(getBaseUrl(), restTemplate, taskBuilder);
    }
}
