@view-projects
Feature: Visualizar lista de proyectos
  Como Maximiliano Gant
  Quiero visualizar proyectos en formato tabla
  Para que pueda revisar información detallada de múltiples proyectos

  @view-project-list
  Scenario: Visualizar lista vacía de proyectos
    Given no existen proyectos en el sistema
    When se solicita la lista de proyectos
    Then se pueden ver 0 proyectos en la lista

  @view-project-list
  Scenario: Visualizar lista con proyectos existentes
    Given existen los siguientes proyectos
      | name           | clientId | type           | billingType       | startDate  | endDate    | status      | leaderId                               |
      | Proyecto Alpha | 1        | DEVELOPMENT    | TIME_AND_MATERIAL | 2040-01-15 | 2040-06-30 | IN_PROGRESS | 123e4567-e89b-12d3-a456-426614174000 |
      | Proyecto Beta  | 2        | IMPLEMENTATION | FIXED_PRICE       | 2040-02-01 | 2040-08-15 | INITIATED   | 123e4567-e89b-12d3-a456-426614174001 |
      | Proyecto Gamma | 3        | DEVELOPMENT    | TIME_AND_MATERIAL | 2040-03-10 | 2040-12-10 | INITIATED   | 123e4567-e89b-12d3-a456-426614174002 |
    When se solicita la lista de proyectos
    Then se pueden ver 3 proyectos en la lista
    And están los proyectos con los siguientes nombres
      | Proyecto Alpha |
      | Proyecto Beta  |
      | Proyecto Gamma |
    And cada proyecto debería mostrar las siguientes columnas
      | id           |
      | name         |
      | clientId     |
      | status       |
      | startDate    |
      | endDate      |
      | tagNames     |


