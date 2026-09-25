@plan-project-dates
Feature: Planificar fechas de proyecto
# Como Gerente de Proyectos
# Quiero establecer fechas de inicio y fin estimadas para un proyecto
# Para que pueda planificar el cronograma de desarrollo

    Scenario: Crear un proyecto con fechas de inicio y fin estimadas
        Given una fecha de inicio "2040-01-01"
        And una fecha de fin estimada "2040-01-31"
        When se crea un proyecto con esas fechas
        Then el proyecto tiene fecha de inicio "2040-01-01"
        And el proyecto tiene fecha de fin estimada "2040-01-31"

    Scenario: Crear un proyecto solo con fecha de inicio estimada
        Given una fecha de inicio "2040-01-01"
        When se crea un proyecto con esa fecha
        Then el proyecto tiene fecha de inicio "2040-01-01"
        And el proyecto no tiene fecha de fin estimada

    Scenario: Crear un proyecto con fechas pasadas
        Given una fecha de inicio "2025-01-01"
        When se crea un proyecto con esa fecha
        Then se rechaza la creación del proyecto por fecha de fin estimada pasada

    # Scenario: Crear un proyecto con fecha de fin anterior a la fecha de inicio
    #     Given una fecha de inicio "2040-01-01"
    #     And una fecha de fin estimada "2039-01-31"
    #     When se crea un proyecto con esas fechas
    #     Then se rechaza la creación del proyecto por fecha de fin estimada pasada

    Scenario: Cambiar fecha de fin estimada
        Given existe un proyecto con una fecha de inicio "2039-01-01"
        And una fecha de fin estimada "2039-01-31"
        When se actualiza el proyecto con una fecha de fin estimada "2040-01-01"
        Then el proyecto tiene fecha de fin estimada "2040-01-01"

    Scenario: Agregar fecha de fin estimada a un proyecto
        Given existe un proyecto con una fecha de inicio "2040-01-01"
        When se actualiza el proyecto con una fecha de fin estimada "2040-01-01"
        Then el proyecto tiene fecha de fin estimada "2040-01-01"

    Scenario: Agregar fechas pasadas
        Given existe un proyecto con una fecha de inicio "2040-01-01"
        When se actualiza el proyecto con una fecha de fin estimada "2024-12-31"
        Then se rechaza la actualización por fecha en el pasado

    # Scenario: Agregar fecha de fin anterior a la fecha de inicio
    #     Given existe un proyecto con una fecha de inicio "2040-01-01"
    #     When se actualiza el proyecto con una fecha de fin estimada "2039-01-31"
    #     Then se rechaza la actualización por fecha anterior a la fecha de inicio