@modify-task-estimated-hours
Feature: Modificar horas estimadas de una tarea
# Como Gerente de Proyectos
# Quiero cambiar las horas estimadas de una tarea
# Para que pueda ajustar las estimaciones según el progreso real

    Background:
        Given existe una tarea con horas estimadas
        | name           | ticketId | status        | estimatedHours | assignedResourceId |
        | Tarea Alpha    | 1001     | IN_PROGRESS   | 8              | 123e4567-e89b-12d3-a456-426614174001 |

    Scenario: Modificar horas estimadas a una tarea
        When se solicita la modificación de las horas estimadas a 10
    Then la tarea tiene 10 horas estimadas

    Scenario: Modificar horas estimadas a un valor negativo
        When se solicita la modificación de las horas estimadas a -1
        Then se rechaza la modificación de las horas estimadas