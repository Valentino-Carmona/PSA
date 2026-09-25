@search-projects
Feature: Buscar y filtrar proyectos
# Como usuario del sistema
# Quiero buscar y filtrar proyectos por diferentes criterios
# Para que pueda encontrar proyectos específicos rápidamente

# Criterios de Aceptación:

#     Puedo filtrar por nombre, tipo, estado, tag (singular)
#     Puedo buscar proyectos por nombre
#     Puedo combinar filtros y busqueda

Background: Configuración inicial de proyectos para búsqueda
  Given existen los siguientes proyectos para buscar
    | name                    | clientId | type          | billingType | startDate   | endDate     | status       | leaderId | tags |
    | Sistema de Gestión ERP  | 1        | DEVELOPMENT   | FIXED_PRICE | 2040-01-15  | 2040-06-30  | IN_PROGRESS  | 123e4567-e89b-12d3-a456-426614174000 | ["ERP", "Sistema"] |
    | Portal Web Corporativo  | 2        | IMPLEMENTATION| TIME_AND_MATERIAL| 2040-02-01 | 2040-05-15  | IN_PROGRESS  | 123e4567-e89b-12d3-a456-426614174001 | ["Web", "Portal"] |
    | App Móvil Cliente       | 1        | DEVELOPMENT   | FIXED_PRICE | 2040-03-01  | 2040-08-31  | INITIATED    | 123e4567-e89b-12d3-a456-426614174002 | ["Móvil", "App"] |
    | Migración Base de Datos | 3        | IMPLEMENTATION| TIME_AND_MATERIAL| 2040-01-01 | 2040-04-30  | TRANSITION   | 123e4567-e89b-12d3-a456-426614174003 | ["BD", "Migración"] |
    | Dashboard Analytics     | 2        | DEVELOPMENT   | FIXED_PRICE | 2040-02-15  | 2040-07-31  | IN_PROGRESS  | 123e4567-e89b-12d3-a456-426614174004 | ["Analytics", "Dashboard"] |

Rule: Los usuarios pueden buscar proyectos por nombre completo o parcial
  
  Example: Búsqueda por nombre completo exacto
    When el usuario busca proyectos por nombre completo "Sistema de Gestión ERP"
    Then debe obtener exactamente 1 proyectos por la busqueda
    And el proyecto obtenido debe ser "Sistema de Gestión ERP"
  
  Example: Búsqueda por letras
    When el usuario busca proyectos por nombre parcial "Portal"
    Then debe obtener exactamente 1 proyectos por la busqueda
    And el proyecto obtenido debe ser "Portal Web Corporativo"
  
  Example: Búsqueda por nombre parcial - prefijo
    When el usuario busca proyectos por nombre parcial "App"
    Then debe obtener exactamente 1 proyectos por la busqueda
    And el proyecto obtenido debe ser "App Móvil Cliente"

  Example: Búsqueda case-insensitive
    When el usuario busca proyectos por nombre parcial "sistema"
    Then debe obtener exactamente 1 proyectos por la busqueda
    And el proyecto obtenido debe ser "Sistema de Gestión ERP"
  
  Example: Búsqueda sin resultados
    When el usuario busca proyectos por nombre parcial "Mobile"
    Then debe obtener exactamente 0 proyectos por la busqueda

  Example: Búsqueda con texto vacío
    When el usuario busca proyectos por nombre parcial ""
    Then debe obtener todos los proyectos del sistema por la busqueda

Rule: Los usuarios pueden filtrar proyectos por tipo
    
  Example: Filtrar proyectos de desarrollo
    When el usuario filtra los proyectos por tipo "DEVELOPMENT"
    Then debe obtener exactamente 3 proyectos
    And los proyectos obtenidos deben ser:
      | name                    | type          |
      | Sistema de Gestión ERP  | DEVELOPMENT   |
      | App Móvil Cliente       | DEVELOPMENT   |
      | Dashboard Analytics     | DEVELOPMENT   |

  Example: Filtrar proyectos de implementación
    When el usuario filtra los proyectos por tipo "IMPLEMENTATION"
    Then debe obtener exactamente 2 proyectos
    And los proyectos obtenidos deben ser:
      | name                    | type          |
      | Portal Web Corporativo  | IMPLEMENTATION|
      | Migración Base de Datos | IMPLEMENTATION|

Rule: Los usuarios pueden filtrar proyectos por estado
    
  Example: Filtrar proyectos en progreso
    When el usuario filtra los proyectos por estado "IN_PROGRESS"
    Then debe obtener exactamente 3 proyectos
    And los proyectos obtenidos deben ser:
      | name                    | status       |
      | Sistema de Gestión ERP  | IN_PROGRESS  |
      | Portal Web Corporativo  | IN_PROGRESS  |
      | Dashboard Analytics     | IN_PROGRESS  |

  Example: Filtrar proyectos iniciados
    When el usuario filtra los proyectos por estado "INITIATED"
    Then debe obtener exactamente 1 proyectos
    And el proyecto obtenido debe ser "App Móvil Cliente"

  Example: Filtrar proyectos en transición
    When el usuario filtra los proyectos por estado "TRANSITION"
    Then debe obtener exactamente 1 proyectos
    And el proyecto obtenido debe ser "Migración Base de Datos"

Rule: Los usuarios pueden filtrar proyectos por tag

  Example: Filtrar proyectos por tag "ERP"
    When el usuario filtra los proyectos por tag "ERP"
    Then debe obtener exactamente 1 proyectos
    And el proyecto obtenido debe ser "Sistema de Gestión ERP"

  Example: Filtrar proyectos por tag "Web"
    When el usuario filtra los proyectos por tag "Web"
    Then debe obtener exactamente 1 proyectos
    And el proyecto obtenido debe ser "Portal Web Corporativo"

  Example: Filtrar por tag inexistente
    When el usuario filtra los proyectos por tag "Inexistente"
    Then debe obtener exactamente 0 proyectos

Rule: Los usuarios pueden aplicar múltiples filtros simultáneamente para búsquedas precisas

  Example: Filtrar por tipo y estado
    When el usuario aplica los siguientes filtros de proyecto:
      | tipo     | estado      |
      | DEVELOPMENT | IN_PROGRESS |
    Then debe obtener exactamente 2 proyectos
    And los proyectos obtenidos deben ser:
      | name                    | tipo          | estado       |
      | Sistema de Gestión ERP  | DEVELOPMENT   | IN_PROGRESS  |
      | Dashboard Analytics     | DEVELOPMENT   | IN_PROGRESS  |

  Example: Filtrar por estado y tag
    When el usuario aplica los siguientes filtros de proyecto:
      | estado      | tag        |
      | IN_PROGRESS | Web        |
    Then debe obtener exactamente 1 proyectos
    And el proyecto obtenido debe ser "Portal Web Corporativo"

  Example: Filtros combinados sin resultados
    When el usuario aplica los siguientes filtros de proyecto:
      | tipo          | estado      |
      | IMPLEMENTATION| INITIATED   |
    Then debe obtener exactamente 0 proyectos

