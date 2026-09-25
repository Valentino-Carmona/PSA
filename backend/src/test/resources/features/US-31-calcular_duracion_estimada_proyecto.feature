@calculate-estimated-duration-project
Feature: Calcular duración estimada de un proyecto
# Como Gerente de Proyectos
# Quiero calcular la duración estimada de un proyecto en base a sus tareas
# Para que pueda controlar el tiempo estimado de un proyecto

# Criterios de Aceptación:

#     Puedo calcular la duración estimada de un proyecto en base a la suma de las horas estimadas de sus tareas

    Background:
        Given existe un proyecto nuevo con tareas
        | name           | ticketId | status        | estimatedHours | assignedResourceId |
        | Tarea Alpha    | 1001     | IN_PROGRESS   | 8              | 123e4567-e89b-12d3-a456-426614174001 |
        | Tarea Beta     | 1002     | IN_PROGRESS   | 4              | 123e4567-e89b-12d3-a456-426614174002 |

    Scenario: Calcular duración estimada de un proyecto con tareas 
        When se solicita la duración estimada del proyecto
        Then se muestra la duración estimada del proyecto como la suma de las horas estimadas de sus tareas

    
    Scenario: Agregar una tarea a un proyecto modifica la duración estimada del proyecto
        Given se agrega una tarea al proyecto con 15 horas estimadas
        When se solicita la duración estimada del proyecto
        Then se muestra la duración estimada del proyecto como la suma de las horas estimadas de sus tareas

    Scenario: Modificar las horas estimadas de una tarea modifica la duración estimada del proyecto
        Given se le suman 10 horas a una tarea del proyecto
        When se solicita la duración estimada del proyecto
        Then se muestra la duración estimada del proyecto como la suma de las horas estimadas de sus tareas