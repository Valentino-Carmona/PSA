# Makefile para el proyecto PSA API

-include .env
export $(shell [ -f .env ] && sed 's/=.*//' .env)

# Variables
PROJECT_NAME := proyecto-api
MAVEN := ./mvnw
DOTENV := .env

.PHONY: help dev clean install test check-env

help:  ## Muestra esta ayuda
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

dev: check-env clean install  ## Inicia la aplicación en modo desarrollo
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
	@$(MAVEN) test -Dspring.flyway.enabled=false

check-env:  ## Verifica que el archivo .env exista
	@if [ ! -f $(DOTENV) ]; then \
		echo "Error: El archivo $(DOTENV) no existe. Crea uno basado en .env.example"; \
		exit 1; \
	fi

flyway-clean: check-env  ## Limpia la base de datos (Flyway)
	@echo "Limpiando la base de datos con Flyway..."
	@$(MAVEN) flyway:clean -Dflyway.url=jdbc:postgresql://${PGHOST}/${PGDATABASE}?sslmode=require -Dflyway.user=${PGUSER} -Dflyway.password=${PGPASSWORD}

flyway-repair: check-env
	@echo "Reparando la base de datos con Flyway..."
	@$(MAVEN) flyway:repair -Dflyway.url=jdbc:postgresql://${PGHOST}/${PGDATABASE}?sslmode=require -Dflyway.user=${PGUSER} -Dflyway.password=${PGPASSWORD}

flyway-migrate: check-env
	@echo "Aplicando migraciones con Flyway..."
	@$(MAVEN) flyway:migrate -Dflyway.url=jdbc:postgresql://$(PGHOST)/$(PGDATABASE)?sslmode=require -Dflyway.user=$(PGUSER) -Dflyway.password=$(PGPASSWORD)

flyway-info: check-env
	@echo "Mostrando información de migraciones Flyway..."
	@$(MAVEN) flyway:info -Dflyway.url=jdbc:postgresql://$(PGHOST)/$(PGDATABASE)?sslmode=require -Dflyway.user=$(PGUSER) -Dflyway.password=$(PGPASSWORD)

db-reset: flyway-clean flyway-migrate  ## Resetea la base de datos (limpia y migra)

setup:
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo "Archivo .env creado. Por favor, edítalo con tus valores reales antes de continuar."; \
	else \
		echo "El archivo .env ya existe."; \
	fi

# Construir la imagen Docker
build:
	@if [ ! -f .env ]; then \
		echo "Error: Archivo .env no encontrado. Ejecuta 'make setup' primero."; \
		exit 1; \
	fi
	docker-compose build

# Levantar los servicios
up:
	@if [ ! -f .env ]; then \
		echo "Error: Archivo .env no encontrado. Ejecuta 'make setup' primero."; \
		exit 1; \
	fi
	docker-compose up -d

# Detener los servicios
down:
	docker-compose down

# Reiniciar los servicios
restart:
	docker-compose restart

# Reconstruir y levantar (útil para cambios en código)
rebuild:
	docker-compose down
	docker-compose build --no-cache
	docker-compose up -d