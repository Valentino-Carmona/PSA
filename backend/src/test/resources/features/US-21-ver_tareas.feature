@view-tasks
Feature: Visualizar lista de tareas
# Como miembro del equipo
# Quiero visualizar las tareas de un proyecto en formato lista
# Para que pueda controlar el estado y distribución del trabajo

Scenario: Visualizar lista vacía de tareas
  Given existe un proyecto vacio
  When se solicita la lista de tareas del proyecto
  Then se pueden ver 0 tareas en la lista

Scenario: Visualizar lista con tareas existentes
  Given existe un proyecto vacio
  And existen las siguientes tareas en el proyecto
    | name           | ticketId | status      | estimatedHours | assignedResourceId |
    | Tarea Alpha    | 1001     | IN_PROGRESS | 8              | 123e4567-e89b-12d3-a456-426614174000 |
    | Tarea Beta     | 1002     | TO_DO       | 4              | 123e4567-e89b-12d3-a456-426614174001 |
    | Tarea Gamma    | 1003     | DONE        | 12             | 123e4567-e89b-12d3-a456-426614174002 |
  When se solicita la lista de tareas del proyecto
  Then se pueden ver 3 tareas en la lista
  And están las tareas con los siguientes nombres
    | Tarea Alpha |
    | Tarea Beta  |
    | Tarea Gamma |
  And cada tarea debería mostrar las siguientes columnas
    | id                 |
    | name               |
    | projectId          |
    | ticketId           |
    | status             |
    | estimatedHours     |
    | assignedResourceId |
    | tagNames           |
