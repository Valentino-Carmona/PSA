@tag-project
Feature: Etiquetar proyectos
# Como Gerente de Proyectos
# Quiero asignar etiquetas (tags) a un proyecto
# Para que pueda clasificar, describir y filtrar proyectos eficientemente

# Criterios de Aceptación:

    # Puedo crear nuevas etiquetas
    # Puedo guardar el proyecto sin etiquetas (opcional)
    # Puedo remover o agregar etiquetas a un proyecto existente

Scenario: Crear un proyecto con etiquetas
    When el usuario crea un proyecto con las siguientes etiquetas
        | pasto |
        | armador |
        | implementacion |
    Then el proyecto debe ser creado con las etiquetas
        | pasto |
        | armador |
        | implementacion |

Scenario: Quitar etiquetas a un proyecto
    Given existe un proyecto con las siguientes etiquetas
        | pasto |
        | armador |
        | diseño |
        | grupal |
    When el usuario quita las siguientes etiquetas
        | pasto |
        | diseño |
    Then el proyecto debe tener las etiquetas
        | armador |
        | grupal |

Scenario: Agregar etiquetas a un proyecto
    Given existe un proyecto con las siguientes etiquetas
        | pasto |
        | diseño |
        | grupal |
    When el usuario agrega las siguientes etiquetas
        | armador |
    Then el proyecto debe tener las etiquetas
        | pasto |
        | armador |

Scenario: Agregar una etiqueta que ya existe
    Given existe un proyecto con las siguientes etiquetas
        | pasto |
    When el usuario agrega la siguiente etiqueta repetida
        | pasto |
    Then se rechaza la operación porque la etiqueta ya existe dentro del proyecto
    And el proyecto debe tener las etiquetas
        | pasto |

Scenario: Quitar una etiqueta que no existe
    Given existe un proyecto con las siguientes etiquetas
        | pasto |
    When el usuario quita la siguiente etiqueta que no existe
        | armador |
    Then se rechaza la operación porque la etiqueta no existe dentro del proyecto
    And el proyecto debe tener las etiquetas
        | pasto |
