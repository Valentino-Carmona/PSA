package com.psa.proyecto_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTagTest {

    private TaskTag taskTag;
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(10L);
        taskTag = new TaskTag("Frontend", task);
        taskTag.setId(1L);
    }

    @Test
    void constructor_ValidParameters_CreatesTaskTag() {
        assertEquals("Frontend", taskTag.getTagName());
        assertEquals(task, taskTag.getTask());
    }

    @Test
    void hasTagName_MatchingName_ReturnsTrue() {
        assertTrue(taskTag.hasTagName("Frontend"));
        assertTrue(taskTag.hasTagName("frontend"));
        assertTrue(taskTag.hasTagName(" FRONTEND "));
    }

    @Test
    void hasTagName_DifferentName_ReturnsFalse() {
        assertFalse(taskTag.hasTagName("Backend"));
    }

    @Test
    void hasTagName_NullName_ReturnsFalse() {
        assertFalse(taskTag.hasTagName(null));
    }

    @Test
    void updateTagName_ValidName_UpdatesSuccessfully() {
        taskTag.updateTagName("UI");
        assertEquals("UI", taskTag.getTagName());
    }

    @Test
    void updateTagName_NullOrEmpty_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> taskTag.updateTagName(null));
        assertThrows(IllegalArgumentException.class, () -> taskTag.updateTagName("   "));
    }

    @Test
    void belongsToTask_MatchingTaskId_ReturnsTrue() {
        assertTrue(taskTag.belongsToTask(10L));
    }

    @Test
    void belongsToTask_DifferentTaskId_ReturnsFalse() {
        assertFalse(taskTag.belongsToTask(99L));
    }

    @Test
    void belongsToTask_NullTask_ReturnsFalse() {
        taskTag.setTask(null);
        assertFalse(taskTag.belongsToTask(10L));
    }

    @Test
    void equals_And_HashCode() {
        TaskTag tag1 = new TaskTag();
        tag1.setId(1L);

        TaskTag tag2 = new TaskTag();
        tag2.setId(1L);

        TaskTag tag3 = new TaskTag();
        tag3.setId(2L);

        assertEquals(tag1, tag1);
        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag1, null);
        assertNotEquals(tag1, new Object());

        TaskTag tag4 = new TaskTag();
        TaskTag tag5 = new TaskTag();
        assertEquals(tag4, tag5);
        assertNotEquals(tag1, tag4);
        assertNotEquals(tag4, tag1);

        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertEquals(tag4.hashCode(), tag5.hashCode());
    }
}
