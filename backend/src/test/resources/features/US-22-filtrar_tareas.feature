@filter-tasks
Feature: Filtrar tareas de proyecto
  Como usuario del sistema
  Quiero filtrar las tareas de un proyecto por diferentes criterios
  Para que pueda encontrar tareas específicas rápidamente y gestionar eficientemente mi trabajo

  Background: Configuración inicial de tareas para filtrado
    Given existe un proyecto con las siguientes tareas con nombres unicos:
      | name                    | status      | ticketId |
      | Análisis de requisitos  | TO_DO       | 1 |
      | Diseño de base de datos | IN_PROGRESS | 2 |
      | Implementar API REST    | TO_DO       | 3 |
      | Testing unitario        | DONE        | 3 | 
      | Configurar CI/CD        | IN_PROGRESS | 2 |
      | Documentación técnica   | DONE        | 4 |
      | Frontend React          | TO_DO       | 5 |
      | Integración backend     | IN_PROGRESS | 6 |
  
  Rule: Los usuarios pueden filtrar tareas por su estado actual
    
    Example: Filtrar tareas pendientes (TO_DO)
      When el usuario filtra las tareas por estado "TO_DO"
      Then debe obtener exactamente 3 tareas
      And las tareas obtenidas deben ser:
        | name                   | status |
        | Análisis de requisitos | TO_DO  |
        | Implementar API REST   | TO_DO  |
        | Frontend React         | TO_DO  |

    Example: Filtrar tareas en progreso (IN_PROGRESS)
      When el usuario filtra las tareas por estado "IN_PROGRESS"
      Then debe obtener exactamente 3 tareas
      And las tareas obtenidas deben ser:
        | name                   | status |
        | Diseño de base de datos | IN_PROGRESS  |
        | Configurar CI/CD        | IN_PROGRESS  |
        | Integración backend     | IN_PROGRESS  |

    Example: Filtrar tareas completadas (DONE)
      When el usuario filtra las tareas por estado "DONE"
      Then debe obtener exactamente 2 tareas
      And las tareas obtenidas deben ser:
        | name                   | status |
        | Testing unitario       | DONE   |
        | Documentación técnica  | DONE   |

    Example: Filtrar por estado inexistente
      When el usuario filtra las tareas por estado "CANCELLED"
      Then la operación se rechaza por estado no válido
  
  Rule: Los usuarios pueden filtrar tareas por ticket ID

    Example: Filtrar tareas del ticket 1
      When el usuario filtra las tareas por ticketId "1"
      Then debe obtener exactamente 1 tareas
      And las tareas obtenidas deben ser:
        | name                   | ticketId |
        | Análisis de requisitos  | 1        |

    Example: Filtrar tareas del ticket 2    
      When el usuario filtra las tareas por ticketId "2"
      Then debe obtener exactamente 2 tareas
      And las tareas obtenidas deben ser:
        | name                    | ticketId |
        | Diseño de base de datos | 2        |
        | Configurar CI/CD        | 2        |

    Example: Filtrar por ticket ID inexistente
      When el usuario filtra las tareas por ticketId "999"
      Then debe obtener exactamente 0 tareas

  Rule: Los usuarios pueden buscar tareas por nombre completo o parcial

    Example: Búsqueda por nombre completo exacto
      When el usuario busca tareas por nombre completo "Testing unitario"
      Then debe obtener exactamente 1 tareas
      And la tarea obtenida debe ser "Testing unitario"

    Example: Búsqueda por letras
      When el usuario busca tareas por nombre parcial "end"
      Then debe obtener exactamente 2 tareas
      And las tareas obtenidas deben ser:
        | name                    |
        | Frontend React         |
        | Integración backend     |

    Example: Búsqueda por nombre parcial - prefijo
      When el usuario busca tareas por nombre parcial "Config"
      Then debe obtener exactamente 1 tareas
      And la tarea obtenida debe ser "Configurar CI/CD"

    Example: Búsqueda case-insensitive
      When el usuario busca tareas por nombre parcial "frontend"
      Then debe obtener exactamente 1 tareas
      And la tarea obtenida debe ser "Frontend React"

    Example: Búsqueda sin resultados
      When el usuario busca tareas por nombre parcial "Mobile"
      Then debe obtener exactamente 0 tareas

    Example: Búsqueda con texto vacío
      When el usuario busca tareas por nombre parcial ""
      Then debe obtener todas las tareas del proyecto

  
  Rule: Los usuarios pueden aplicar múltiples filtros simultáneamente para búsquedas precisas

    Example: Filtrar por estado y recurso asignado
      When el usuario aplica los siguientes filtros:
        | status   | ticketId |
        | TO_DO    | 3        |
      Then debe obtener exactamente 1 tareas
      And la tarea obtenida debe ser "Implementar API REST"

    Example: Filtros combinados sin resultados
      When el usuario aplica los siguientes filtros:
        | status   | ticketId |
        | DONE     | 1        |
      Then debe obtener exactamente 0 tareas