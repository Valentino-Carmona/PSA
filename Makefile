# Makefile para el proyecto PSA API

-include .env
export $(shell [ -f .env ] && sed 's/=.*//' .env)

# Variables
PROJECT_NAME := proyecto-api
MAVEN := ./mvnw
DOTENV := .env

DOCKER_COMPOSE := docker compose
FLYWAY_ARGS := -Dflyway.url=jdbc:postgresql://$$(PGHOST)/$$(PGDATABASE)?sslmode=require -Dflyway.user=$$(PGUSER) -Dflyway.password=$$(PGPASSWORD)

.PHONY: help dev clean install test check-env flyway-clean flyway-repair flyway-migrate flyway-info db-reset setup build up down restart rebuild

help:  ## Muestra esta ayuda
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

dev: check-env  ## Inicia la aplicación en modo desarrollo
	@echo "Iniciando la aplicación en modo desarrollo..."
	@$(MAVEN) spring-boot:run

clean:  ## Limpia el proyecto
	@echo "Limpiando el proyecto..."
	@$(MAVEN) clean

install:  ## Instala dependencias y compila
	@echo "Instalando dependencias..."
	@$(MAVEN) install

test:  ## Ejecuta los tests
	@echo "Ejecutando tests..."
	@$(MAVEN) test

check-env:  ## Verifica que el archivo .env exista
	@if [ ! -f $(DOTENV) ]; then \
		echo "Error: El archivo $(DOTENV) no existe. Crea uno basado en .env.example"; \
		exit 1; \
	fi

flyway-clean: check-env  ## Limpia la base de datos (Flyway)
	@echo "Limpiando la base de datos con Flyway..."
	@$(MAVEN) flyway:clean $(FLYWAY_ARGS)

flyway-repair: check-env
	@echo "Reparando la base de datos con Flyway..."
	@$(MAVEN) flyway:repair $(FLYWAY_ARGS)

flyway-migrate: check-env
	@echo "Aplicando migraciones con Flyway..."
	@$(MAVEN) flyway:migrate $(FLYWAY_ARGS)

flyway-info: check-env
	@echo "Mostrando información de migraciones Flyway..."
	@$(MAVEN) flyway:info $(FLYWAY_ARGS)

db-reset: flyway-clean flyway-migrate  ## Resetea la base de datos (limpia y migra)

setup:
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo "Archivo .env creado. Por favor, edítalo con tus valores reales antes de continuar."; \
	else \
		echo "El archivo .env ya existe."; \
	fi

# Construir la imagen Docker
build: check-env
	$(DOCKER_COMPOSE) build

# Levantar los servicios
up: check-env
	$(DOCKER_COMPOSE) up -d

# Detener los servicios
down:
	$(DOCKER_COMPOSE) down

# Reiniciar los servicios
restart:
	$(DOCKER_COMPOSE) restart

# Reconstruir y levantar (útil para cambios en código)
rebuild: down build up