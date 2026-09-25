@project-management
Feature: Crear Proyecto
  Como usuario del sistema
  Quiero crear un nuevo proyecto
  Para poder gestionar los proyectos de la empresa

  @crear-proyecto
  Scenario: Crear un proyecto con información completa
    Given se tiene la siguiente información del proyecto:
      | name      | clientId | type      | billingType    | startDate  | endDate    | leaderId                                |
      | <name>    | <client> | <type>    | <billing>      | <start>    | <end>      | <leader>                                |
    When se envía la solicitud para crear el proyecto
    Then el proyecto debería ser creado exitosamente con los siguientes datos:
      | name      | clientId | type      | billingType    | startDate  | endDate    | leaderId |
      | <name>    | <client> | <type>    | <billing>      | <start>    | <end>      | <leader> |
    And el proyecto debería tener un ID único
    And el proyecto debería tener un estado inicial "INITIATED"
    And el proyecto debería tener 0 horas estimadas

    Examples:
      | name             | client | type           | billing           | start      | end        | leader                                  |
      | Proyecto Dev TM  | 1      | DEVELOPMENT    | TIME_AND_MATERIAL | 2030-06-15 | 2030-12-31 | e1a8216f-7c66-4888-9668-f7c4145cce7e  |
      | Proyecto Dev FP  | 2      | DEVELOPMENT    | FIXED_PRICE       | 2030-01-01 | 2030-06-30 | e1a8216f-7c66-4888-9668-f7c4145cce7e  |
      | Proyecto Impl TM | 3      | IMPLEMENTATION | TIME_AND_MATERIAL | 2030-07-01 | 2030-12-15 | e1a8216f-7c66-4888-9668-f7c4145cce7e  |
      | Proyecto Impl FP | 4      | IMPLEMENTATION | FIXED_PRICE       | 2030-03-15 | 2030-09-30 | e1a8216f-7c66-4888-9668-f7c4145cce7e  |

  @crear-proyecto
  Scenario Outline: Crear un proyecto con información mínima
    Given se tiene la siguiente información del proyecto:
      | name      | clientId | type        | billingType        | startDate | 
      | <name>    | <clientId> | <type>      | <billingType>      | <date>    |
    When se envía la solicitud para crear el proyecto
    Then el proyecto debería ser creado exitosamente con los siguientes datos:
      | name      | clientId   | type      | billingType    | startDate  | 
      | <name>    | <clientId> | <type>    | <billingType>      | <date>    |
    And el proyecto debería tener un ID único
    And el proyecto debería tener un estado inicial "INITIATED"
    And el proyecto debería tener 0 horas estimadas

    Examples:
      | name          | clientId | type           | billingType        | date       |
      | Proyecto Dev1 | 1        | DEVELOPMENT    | TIME_AND_MATERIAL  | 2030-06-15 |
      | Proyecto Dev2 | 2        | DEVELOPMENT    | FIXED_PRICE        | 2030-01-20 |
      | Proyecto Imp1 | 3        | IMPLEMENTATION | TIME_AND_MATERIAL  | 2030-02-28 |
      | Proyecto Imp2 | 4        | IMPLEMENTATION | FIXED_PRICE        | 2030-03-30 |

  @crear-proyecto
  Scenario Outline: Intentar crear proyectos con información incompleta
    Given se tiene la siguiente información incompleta del proyecto:
      | name      | clientId | type        | billingType | startDate |
      | <name>    | <client> | <type>      | <billing>   | <start>   |
    When se envía la solicitud para crear el proyecto
    Then se rechaza la creación del proyecto

    Examples:
      | name       | client | type        | billing           | start      | description                |
      |            | 1      | DEVELOPMENT | TIME_AND_MATERIAL | 2030-06-15 | Sin nombre                |
      | Proyecto 3 |        | DEVELOPMENT | TIME_AND_MATERIAL | 2030-06-15 | Sin cliente               |
      | Proyecto 4 | 1      |             | TIME_AND_MATERIAL | 2030-06-15 | Sin tipo                  |
      | Proyecto 5 | 1      | DEVELOPMENT |                   | 2030-06-15 | Sin tipo de facturación   |
      | Proyecto 6 | 1      | DEVELOPMENT | TIME_AND_MATERIAL |            | Sin fecha de inicio       |

  @crear-proyecto
  Scenario: No se puede crear un proyecto con fecha de inicio en el pasado
    Given se tiene la siguiente información del proyecto:
      | name      | clientId | type        | billingType        | startDate  | endDate    | leaderId                                |
      | Proyecto  | 1        | DEVELOPMENT | TIME_AND_MATERIAL  | 2023-01-01 | 2023-12-31 | e1a8216f-7c66-4888-9668-f7c4145cce7e  |
    When se envía la solicitud para crear el proyecto
    Then se rechaza la creación del proyecto
