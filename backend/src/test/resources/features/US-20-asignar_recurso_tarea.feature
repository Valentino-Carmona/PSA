@assign-resource-to-task
Feature: Asignar recurso a tarea    
# Como Gerente de Proyectos
# Quiero asignar un recurso a una tarea
# Para que pueda distribuir la carga de trabajo del equipo

    Scenario: Asignar recurso a tarea existente
    Given existe la siguiente tarea sin recurso asignado
    When se solicita la asignación del recurso "123e4567-e89b-12d3-a456-426614174000" a la tarea
    Then el recurso se asigna correctamente a la tarea

    Scenario: Asignar recurso a tarea con recurso asignado
    Given existe la siguiente tarea con recurso asignado
    When se solicita la asignación del recurso "123e4567-e89b-12d3-a456-426614174000" a la tarea
    Then el recurso se asigna correctamente a la tarea