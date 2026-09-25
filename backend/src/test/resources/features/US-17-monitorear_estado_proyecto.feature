@monitor-project-progress
Feature: Monitorear progreso de un proyecto
# Como Gerente de Proyectos
# Quiero actualizraa el estado del proyecto según el progreso de sus tareas
# Para que pueda evitar discrepancias de estado entre un proyecto y sus tareas
  
  Scenario: Iniciar una tarea actualiza el proyecto
    Given existe un proyecto con estado "INITIATED"
    And existe una tarea con el estado "TO_DO"
    When el usuario inicia la tarea
    Then la tarea cambia a estado "IN_PROGRESS"
    And el proyecto actualiza su estado a "IN_PROGRESS"

  Scenario: Completar todas las tareas actualiza el estado del proyecto
    Given existe un proyecto con estado "INITIATED"
    And existe una tarea con el estado "IN_PROGRESS"
    When el usuario completa la tarea
    Then la tarea cambia a estado "DONE"
    And el proyecto actualiza su estado a "TRANSITION"

  Scenario: Proyecto con múltiples tareas permanece en progreso hasta completarlas todas
    Given existe un proyecto con estado "INITIATED"
    And existen tareas con los siguientes estados:
      | taskName | status      |
      | Tarea 1  | DONE        |
      | Tarea 2  | IN_PROGRESS |
    When el usuario completa la tarea "Tarea 2"
    Then la tarea "Tarea 2" cambia a estado "DONE"
    And el proyecto actualiza su estado a "TRANSITION"

  Scenario: Completar la última tarea activa actualiza el proyecto a finalizado
    Given existe un proyecto con estado "IN_PROGRESS"
    When completa todas sus tareas
    Then todas las tareas cambian a estado "DONE"
    And el proyecto actualiza su estado a "TRANSITION"

  Scenario: Proyecto regresa a estado inicial sin tareas activas
    Given existe un proyecto con estado "INITIATED"
    And no existen tareas para el proyecto
    Then el proyecto debe tener estado "INITIATED"