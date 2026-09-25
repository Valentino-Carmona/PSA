@view-task-detail
Feature: Visualizar detalle de tarea
# Como Maximiliano Gant
# Quiero acceder a una vista detallada de una tarea
# Para que pueda ver todos sus atributos y gestionar su progreso


Scenario: Visualizar detalle de tarea existente
  Given existe una tarea detallada con los siguientes datos
    | name           | ticketId | status       | assignedResourceId |
    | Tarea Alpha    | 1001     | IN_PROGRESS   | 123e4567-e89b-12d3-a456-426614174001 |
  When se solicita el detalle completo de la tarea
  Then se muestra el detalle completo de la tarea
  And se muestran todos los atributos de la tarea
    | id                   |
    | name                 |
    | projectId            |
    | ticketId             |
    | status               |
    | estimatedHours       |
    | assignedResourceId   |
    | tagNames             |


Scenario: Intentar visualizar tarea inexistente
  Given no existe una tarea
  When se solicita el detalle completo de la tarea
  Then se recibe un error de tarea no encontrada 