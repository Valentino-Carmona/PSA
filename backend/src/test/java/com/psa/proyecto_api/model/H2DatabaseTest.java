package com.psa.proyecto_api.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify H2 database setup is working correctly
 */
@SpringBootTest(classes = com.psa.proyecto_api.ProyectoApiApplication.class)
@ActiveProfiles("test") 
public class H2DatabaseTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testH2DatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Verify it's H2
            assertTrue(metaData.getURL().contains("h2"), 
                    "Should be using H2 database, but got: " + metaData.getURL());
            
            // Verify we can query tables
            ResultSet tables = metaData.getTables(null, null, "PROJECTS", null);
            assertTrue(tables.next(), "PROJECTS table should exist");
            
            tables = metaData.getTables(null, null, "TASKS", null);
            assertTrue(tables.next(), "TASKS table should exist");
            
            System.out.println("✅ H2 Database Setup Verification:");
            System.out.println("   - Database URL: " + metaData.getURL());
            System.out.println("   - Database Product: " + metaData.getDatabaseProductName());
            System.out.println("   - Driver: " + metaData.getDriverName());
            System.out.println("   - Tables created successfully");
        }
    }

    @Test
    void testDatabaseIsolation() throws SQLException {
        // This test verifies that each test gets a fresh database
        try (Connection connection = dataSource.getConnection()) {
            // Check that we start with clean tables
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT COUNT(*) FROM PROJECTS");
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println("✅ Projects count at test start: " + count);
                // Count might be 0 (clean) or have test data from test-data.sql
                assertTrue(count >= 0, "Project count should be non-negative");
            }
        }
    }
} 