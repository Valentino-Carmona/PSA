package com.psa.proyecto_api.model.enums;

import com.psa.proyecto_api.exception.OperationNotAllowedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumsTest {

    @Test
    void projectStatus_ValidValues_ReturnsCorrectEnum() {
        assertEquals(ProjectStatus.INITIATED, ProjectStatus.fromString("Iniciado"));
        assertEquals(ProjectStatus.INITIATED, ProjectStatus.fromString("INITIATED"));
        assertEquals(ProjectStatus.IN_PROGRESS, ProjectStatus.fromString("En Progreso"));
        assertEquals(ProjectStatus.IN_PROGRESS, ProjectStatus.fromString("in_progress"));
        assertEquals(ProjectStatus.TRANSITION, ProjectStatus.fromString("Transición"));
    }

    @Test
    void projectStatus_InvalidValue_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> ProjectStatus.fromString("Invalido"));
    }

    @Test
    void projectStatus_NullValue_ReturnsNull() {
        assertNull(ProjectStatus.fromString(null));
    }

    @Test
    void projectStatus_BooleansWorkCorrectly() {
        assertTrue(ProjectStatus.INITIATED.isInitiated());
        assertFalse(ProjectStatus.INITIATED.isActive());
        
        assertTrue(ProjectStatus.IN_PROGRESS.isActive());
        assertFalse(ProjectStatus.IN_PROGRESS.isTransition());
        
        assertTrue(ProjectStatus.TRANSITION.isTransition());
        assertFalse(ProjectStatus.TRANSITION.isActive());
    }

    @Test
    void projectStatus_GeneratedMethodsWork() {
        assertNotNull(ProjectStatus.valueOf("INITIATED"));
        assertTrue(ProjectStatus.values().length > 0);
    }

    @Test
    void projectType_ValidValues_ReturnsCorrectEnum() {
        assertEquals(ProjectType.DEVELOPMENT, ProjectType.fromString("Desarrollo"));
        assertEquals(ProjectType.DEVELOPMENT, ProjectType.fromString("DEVELOPMENT"));
        assertEquals(ProjectType.IMPLEMENTATION, ProjectType.fromString("Implementación"));
        assertEquals(ProjectType.IMPLEMENTATION, ProjectType.fromString("iMpLeMeNtAcIóN"));
    }

    @Test
    void projectType_InvalidValue_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> ProjectType.fromString("Fake"));
    }

    @Test
    void projectType_NullValue_ReturnsNull() {
        assertNull(ProjectType.fromString(null));
    }

    @Test
    void projectType_GeneratedMethodsWork() {
        assertNotNull(ProjectType.valueOf("DEVELOPMENT"));
        assertTrue(ProjectType.values().length > 0);
    }

    @Test
    void projectBillingType_ValidValues_ReturnsCorrectEnum() {
        assertEquals(ProjectBillingType.TIME_AND_MATERIAL, ProjectBillingType.fromString("Tiempo y Materiales"));
        assertEquals(ProjectBillingType.TIME_AND_MATERIAL, ProjectBillingType.fromString("TIME_AND_MATERIAL"));
        assertEquals(ProjectBillingType.FIXED_PRICE, ProjectBillingType.fromString("Precio Fijo"));
        assertEquals(ProjectBillingType.FIXED_PRICE, ProjectBillingType.fromString("fixed_price"));
    }

    @Test
    void projectBillingType_InvalidValue_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> ProjectBillingType.fromString("Gratis"));
    }

    @Test
    void projectBillingType_NullValue_ReturnsNull() {
        assertNull(ProjectBillingType.fromString(null));
    }

    @Test
    void projectBillingType_GeneratedMethodsWork() {
        assertNotNull(ProjectBillingType.valueOf("FIXED_PRICE"));
        assertTrue(ProjectBillingType.values().length > 0);
    }

    @Test
    void taskStatus_ValidValues_ReturnsCorrectEnum() {
        assertEquals(TaskStatus.TO_DO, TaskStatus.fromString("Por Hacer"));
        assertEquals(TaskStatus.TO_DO, TaskStatus.fromString("TO_DO"));
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.fromString("En Progreso"));
        assertEquals(TaskStatus.DONE, TaskStatus.fromString("Completada"));
    }

    @Test
    void taskStatus_InvalidValue_ThrowsException() {
        assertThrows(OperationNotAllowedException.class, () -> TaskStatus.fromString("Canceled"));
    }

    @Test
    void taskStatus_NullValue_ReturnsNull() {
        assertNull(TaskStatus.fromString(null));
    }

    @Test
    void taskStatus_GeneratedMethodsWork() {
        assertNotNull(TaskStatus.valueOf("TO_DO"));
        assertTrue(TaskStatus.values().length > 0);
    }

    @Test
    void taskStatus_BooleansWorkCorrectly() {
        assertTrue(TaskStatus.TO_DO.isActive());
        assertTrue(TaskStatus.TO_DO.canStart());
        assertTrue(TaskStatus.TO_DO.canComplete());

        assertTrue(TaskStatus.IN_PROGRESS.isActive());
        assertFalse(TaskStatus.IN_PROGRESS.canStart());
        assertTrue(TaskStatus.IN_PROGRESS.canComplete());

        assertFalse(TaskStatus.DONE.isActive());
        assertFalse(TaskStatus.DONE.canStart());
        assertFalse(TaskStatus.DONE.canComplete());
    }
}
