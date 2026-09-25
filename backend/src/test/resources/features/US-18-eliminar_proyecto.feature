@delete-project
Feature: Eliminar proyecto
# Como Gerente de Proyectos
# Quiero eliminar un proyecto
# Para que pueda remover proyectos cancelados o completados
  Scenario: Eliminar proyecto existente
    Given existe un proyecto
    When se solicita la eliminación del proyecto
    Then el proyecto se elimina correctamente
  
  Scenario: Eliminar proyecto elimina sus tareas automáticamente
    Given existe un proyecto
    And existen las siguientes tareas en el proyecto antes de eliminarlo
    | name           | status      | estimatedHours | 
    | Tarea Alpha    | IN_PROGRESS | 8              | 
    | Tarea Beta     | TO_DO       | 4              | 
    | Tarea Gamma    | DONE        | 12             | 
    When se solicita la eliminación del proyecto
    Then el proyecto se elimina correctamente
    And las tareas del proyecto se eliminan correctamente

  Scenario: Intentar eliminar proyecto inexistente
    Given no existe un proyecto de id 999 
    When se solicita la eliminación del proyecto
    Then se rechaza la solicitud porque el proyecto no existe