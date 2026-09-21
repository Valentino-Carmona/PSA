package com.psa.proyecto_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class for integration tests that use H2 database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional  // Ensures each test rolls back
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected DataSource dataSource;



    public void setUp() {
        
        // Verify H2 connection
        try (Connection conn = dataSource.getConnection()) {
            // System.out.println("✅ Connected to H2 database: " + conn.getMetaData().getURL());
        } catch (SQLException e) {
            throw new RuntimeException("❌ Failed to connect to H2 database", e);
        }
    }

    /**
     * Helper method to get the base URL with the current port
     */
    protected String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1";
    }

    /**
     * Helper method to get the full URL for an endpoint
     */
    protected String url(String endpoint) {
        return getBaseUrl() + endpoint;
    }
}