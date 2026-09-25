@view-project-detail
Feature: Visualizar detalle de proyecto
# Como Gerente de Proyectos
# Quiero acceder a una vista detallada de un proyecto
# Para que pueda ver todos sus atributos y tareas asociadas


Scenario: Visualizar detalle de proyecto existente
  Given existe un proyecto detallado con los siguientes datos
    |name           | clientId | type         | billingType       | startDate   | endDate      | status      | leaderId |
    |Proyecto Alpha | 100      | DEVELOPMENT  | FIXED_PRICE       | 2040-01-01  | 2040-06-30   | INITIATED | 123e4567-e89b-12d3-a456-426614174000 |
  And existen las siguientes tareas pertenecientes al proyecto
    | name           | ticketId | status      | estimatedHours | assignedResourceId |
    | Tarea Alpha    |  1001     | IN_PROGRESS | 8              | 123e4567-e89b-12d3-a456-426614174001 |
    | Tarea Beta     | 1002     | TO_DO       | 4              | 123e4567-e89b-12d3-a456-426614174002 |
    | Tarea Gamma    | 1003     | DONE        | 12             | 123e4567-e89b-12d3-a456-426614174003 |
  When se solicita el detalle completo del proyecto
  Then se muestra el detalle completo del proyecto
  And se muestran todos los atributos del proyecto
    | id                 |
    | name               |
    | clientId           |
    | type               |
    | billingType        |
    | startDate          |
    | endDate            |
    | estimatedHours     |
    | status             |
    | leaderId           |
    | tagNames           |
  And se muestran 3 tareas asociadas al proyecto
  And se pueden ver las tareas con los siguientes nombres
    | Tarea Alpha |
    | Tarea Beta  |
    | Tarea Gamma |

Scenario: Visualizar detalle de proyecto sin tareas
  Given existe un proyecto detallado con los siguientes datos
    | name            | clientId | type            | billingType       | startDate   | endDate     | status     | leaderId |
    | Proyecto Beta   | 101      | IMPLEMENTATION  | TIME_AND_MATERIAL | 2040-02-01  | 2040-08-31  | INITIATED | 123e4567-e89b-12d3-a456-426614174004 |
  And no existen tareas en el proyecto
  When se solicita el detalle completo del proyecto
  Then se muestra el detalle completo del proyecto
  And se muestran todos los atributos del proyecto
    | id                 |
    | name               |
    | clientId           |
    | type               |
    | billingType        |
    | startDate          |
    | endDate            |
    | estimatedHours     |
    | status             |
    | leaderId           |
    | tagNames           |
  And se muestran 0 tareas asociadas al proyecto

Scenario: Intentar visualizar proyecto inexistente
  Given no existe un proyecto
  When se solicita el detalle completo del proyecto
  Then se recibe un error de proyecto no encontrado 