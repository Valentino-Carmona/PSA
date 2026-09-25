@crear-tarea
Feature: Crear tarea básica
# Como Gerente de Proyectos
# Quiero crear una tarea dentro de un proyecto
# Para que pueda organizar el trabajo en unidades manejables


  
  Scenario Outline: Crear una nueva tarea con información mínima
    Given existe el proyecto con la siguiente información:
      | name | clientId | type | billingType | startDate |
      | Projecto1 | 1 | DEVELOPMENT | TIME_AND_MATERIAL | 2030-06-15 |
    
    Given se tiene la siguiente información de una tarea:
      | name      | estimatedHours | 
      | <name>    | <hours>        | 
    When el usuario intenta crear una nueva tarea

    Then la tarea se crea correctamente con los siguientes datos:
      | name      | estimatedHours | status |
      | <name>    | <hours>        | TO_DO  |
    And el proyecto tiene una tarea
    And el proyecto contiene la tarea "<name>"
    Examples:
      | name            | hours |
      | Tarea simple 1  | 23    |
  
  Scenario: Crear tareas para un proyecto impacta en sus horas estimadas
    Given existe el proyecto con la siguiente información:
      | name | clientId | type | billingType | startDate |
      | Projecto2 | 1 | DEVELOPMENT | TIME_AND_MATERIAL | 2030-06-15 |
    Given se tiene la siguiente información de tareas:
      | name | estimatedHours | assignedResourceId |
      | Tarea simple 1  | 23          | e1a8216f-7c66-4888-9668-f7c4145cce7e    |
      | Tarea simple 2  | 19          | e1a8216f-7c66-4888-9668-f7c4145cce7e    |

    When el usuario intenta crear las nuevas tareas

    Then las tareas se crean correctamente con los siguientes datos:
      | name | estimatedHours | assignedResourceId | status |
      | Tarea simple 1  | 23          | e1a8216f-7c66-4888-9668-f7c4145cce7e    | TO_DO |
      | Tarea simple 2  | 19          | e1a8216f-7c66-4888-9668-f7c4145cce7e    | TO_DO |
    And el proyecto contiene las tareas "Tarea simple 1" y "Tarea simple 2"
    And el proyecto tiene 42 horas estimadas
  
  Scenario Outline: Crear una tarea con información incompleta
    Given existe el proyecto con la siguiente información:
      | name | clientId | type | billingType | startDate |
      | Projecto3 | 1 | DEVELOPMENT | TIME_AND_MATERIAL | 2030-06-15 |
    Given se tiene la siguiente información incompleta de una tarea:
      | name | estimatedHours |
      | <name>  | <hours>  | 

    When el usuario intenta crear una nueva tarea

    Then se rechaza la creación de la tarea por información incompleta
    Examples:
      | name            | hours |
      | Tarea simple 1  |     |
      |   | 19    |
      |   |   |

  Scenario: Crear una tarea con horas negativas
    Given existe el proyecto con la siguiente información:
      | name | clientId | type | billingType | startDate |
      | Projecto4 | 1 | DEVELOPMENT | TIME_AND_MATERIAL | 2030-06-15 |
    Given se tiene la siguiente información de una tarea:
      | name | estimatedHours |
      | Tarea simple 1  | -1           |
    When el usuario intenta crear una nueva tarea

    Then se rechaza la creación de la tarea por horas negativas
