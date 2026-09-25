@update-task-status
Feature: Cambiar el estado de una tarea
# Como miembro del equipo
# Quiero actualizar el estado de una tarea
# Para que pueda reflejar el progreso del trabajo
  
  Scenario: Iniciar una tarea no iniciada
    Given cambiar estado de tarea: existe una tarea con estado "TO_DO"
    When el usuario intenta iniciar la tarea
    Then la tarea tiene el estado "IN_PROGRESS"

  Scenario: Intentar completar una tarea no iniciada directamente
    Given cambiar estado de tarea: existe una tarea con estado "TO_DO"
    When el usuario intenta completar la tarea
    Then se rechaza la operación por estado inválido
    And el estado de la tarea se mantiene en "TO_DO"

  Scenario: Intentar iniciar una tarea ya en progreso 
    Given cambiar estado de tarea: existe una tarea con estado "IN_PROGRESS"
    When el usuario intenta iniciar la tarea
    Then se rechaza la operación por estado inválido
    And el estado de la tarea se mantiene en "IN_PROGRESS"

  Scenario: Intentar completar una tarea ya completada
    Given cambiar estado de tarea: existe una tarea con estado "DONE"
    When el usuario intenta completar la tarea
    Then se rechaza la operación por estado inválido
    And el estado de la tarea se mantiene en "DONE"

  Scenario: Retroceder una tarea en progreso a no iniciada
    Given cambiar estado de tarea: existe una tarea con estado "IN_PROGRESS"
    When el usuario intenta retroceder la tarea
    Then se rechaza la operación por estado inválido
    And el estado de la tarea se mantiene en "IN_PROGRESS"

  Scenario: Intentar iniciar una tarea completada
    Given cambiar estado de tarea: existe una tarea con estado "DONE"
    When el usuario intenta iniciar la tarea
    Then se rechaza la operación por estado inválido
    And el estado de la tarea se mantiene en "DONE"

  Scenario: Intentar completar una tarea en progreso
    Given cambiar estado de tarea: existe una tarea con estado "IN_PROGRESS"
    When el usuario intenta completar la tarea
    Then la tarea tiene el estado "DONE"