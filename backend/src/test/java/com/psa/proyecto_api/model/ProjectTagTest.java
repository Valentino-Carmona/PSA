package com.psa.proyecto_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTagTest {

    private ProjectTag projectTag;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(10L);
        projectTag = new ProjectTag("Backend", project);
        projectTag.setId(1L);
    }

    @Test
    void constructor_ValidParameters_CreatesProjectTag() {
        assertEquals("Backend", projectTag.getTagName());
        assertEquals(project, projectTag.getProject());
    }

    @Test
    void hasTagName_MatchingName_ReturnsTrue() {
        assertTrue(projectTag.hasTagName("Backend"));
        assertTrue(projectTag.hasTagName("backend"));
        assertTrue(projectTag.hasTagName(" BACKEND "));
    }

    @Test
    void hasTagName_DifferentName_ReturnsFalse() {
        assertFalse(projectTag.hasTagName("Frontend"));
    }

    @Test
    void hasTagName_NullName_ReturnsFalse() {
        assertFalse(projectTag.hasTagName(null));
    }

    @Test
    void updateTagName_ValidName_UpdatesSuccessfully() {
        projectTag.updateTagName("API");
        assertEquals("API", projectTag.getTagName());
    }

    @Test
    void updateTagName_NullOrEmpty_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> projectTag.updateTagName(null));
        assertThrows(IllegalArgumentException.class, () -> projectTag.updateTagName("   "));
    }

    @Test
    void belongsToProject_MatchingProjectId_ReturnsTrue() {
        assertTrue(projectTag.belongsToProject(10L));
    }

    @Test
    void belongsToProject_DifferentProjectId_ReturnsFalse() {
        assertFalse(projectTag.belongsToProject(99L));
    }

    @Test
    void belongsToProject_NullProject_ReturnsFalse() {
        projectTag.setProject(null);
        assertFalse(projectTag.belongsToProject(10L));
    }

    @Test
    void equals_And_HashCode() {
        ProjectTag tag1 = new ProjectTag();
        tag1.setId(1L);

        ProjectTag tag2 = new ProjectTag();
        tag2.setId(1L);

        ProjectTag tag3 = new ProjectTag();
        tag3.setId(2L);

        assertEquals(tag1, tag1);
        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag1, null);
        assertNotEquals(tag1, new Object());

        ProjectTag tag4 = new ProjectTag();
        ProjectTag tag5 = new ProjectTag();
        assertEquals(tag4, tag5);
        assertNotEquals(tag1, tag4);
        assertNotEquals(tag4, tag1);

        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertEquals(tag4.hashCode(), tag5.hashCode());
    }
}
