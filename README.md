# PSA — Módulo de Gestión de Proyectos

![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.0-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-4169E1?style=flat&logo=postgresql&logoColor=white)
![BDD Cucumber](https://img.shields.io/badge/BDD-Cucumber-23D96C?style=flat&logo=cucumber&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=flat&logo=apachemaven&logoColor=white)

---

## 01. Overview

**PSA es una API REST para la gestión de proyectos y tareas**, desarrollada como módulo de backend para el sistema PSA (Project & Squad Administration). Expone endpoints para crear, consultar, filtrar y administrar proyectos y sus tareas asociadas, con soporte para etiquetas, estados, asignación de recursos y cálculo de duración estimada.

### Origen Académico

El proyecto fue desarrollado de forma colaborativa por un equipo de 5 integrantes como él trabajo práctico de la materia **Ingeniería de Software** de la **Universidad de Buenos Aires (FIUBA)**. El ciclo de vida incluyó relevamiento de requerimientos, modelado de dominio, definición de historias de usuario con Gherkin, implementación iterativa y pruebas de integración. Todo fue supervisado por el equipo de profesores de forma semanal mediante reuniones de obtencion de requisitos. Ellos tomaron el rol de: Tomás Bruneleschi (PO) y Leonardo Felici (PM)

### Alcance de la Implementación

El sistema expone una **REST API documentada con Swagger UI** sobre un backend Spring Boot con persistencia en PostgreSQL (Neon). Implementa:

- CRUD de proyectos y tareas con validación de datos de entrada.
- Filtrado por nombre, estado, etiquetas y ticket asociado.
- Integración con un cliente HTTP externo (PSA Client) para sincronizar recursos y datos de otros módulos.
- Migraciones de base de datos gestionadas con Flyway (4 migraciones: esquema de proyectos, tareas, tags e índices).
- Suite de pruebas de integración BDD con 16 archivos `.feature` en Cucumber.

Para la documentación de endpoints disponibles, ver el [Swagger UI en producción](https://squad-07-2025-1c.onrender.com/swagger-ui/index.html).

---

## 02. Dominio

El módulo gestiona dos entidades principales:

- **Project:** representa un proyecto con nombre, estado (`PENDIENTE`, `ACTIVO`, `PAUSADO`, `CANCELADO`, `TERMINADO`), fechas planificadas, cliente y etiquetas.
- **Task:** representa una tarea dentro de un proyecto con asignado posible de horas estimadas, estado (`TODO`, `IN_PROGRESS`, `DONE`), ticket asociado y etiquetas.

La API también consume servicios externos a través de `ExternalApiController` para integrar datos de recursos del sistema PSA.

---

## 03. Arquitectura

El proyecto sigue una **arquitectura en capas** organizada como microservicio REST.

### 3.1. Stack Tecnológico

| Capa | Tecnología |
| :--- | :--- |
| **Lenguaje** | Java 17 |
| **Framework** | Spring Boot 3.5.0, Spring Data JPA, Spring Validation |
| **Persistencia** | PostgreSQL (Neon), Flyway |
| **Documentación API** | Springdoc OpenAPI / Swagger UI |
| **Variables de entorno** | dotenv-java |
| **Build** | Maven 3.x (wrapper `mvnw`) |
| **Testing** | JUnit, Cucumber (BDD) |
| **Reducción de boilerplate** | Lombok 1.18.34 |

### 3.2. Estructura de Paquetes

```text
src/
├── main/
│   ├── java/com/psa/proyecto_api/
│   │   ├── config/            # Configuración global (CORS, beans)
│   │   ├── controller/        # Controladores REST
│   │   │   ├── ProjectController.java
│   │   │   ├── TaskController.java
│   │   │   └── ExternalApiController.java
│   │   ├── service/           # Lógica de negocio (interfaces + impl)
│   │   │   ├── ProjectService.java
│   │   │   ├── TaskService.java
│   │   │   └── ExternalApiService.java
│   │   ├── repository/        # Spring Data JPA
│   │   ├── specification/     # Specs para filtrado dinámico
│   │   ├── model/             # Entidades JPA (Project, Task, Tags, Enums)
│   │   ├── dto/               # Request / Response DTOs
│   │   ├── mapper/            # Conversión entidad ↔ DTO
│   │   ├── exception/         # Manejo global de excepciones
│   │   └── ProyectoApiApplication.java
│   └── resources/
│       ├── application.properties
│       └── db/migration/      # Migraciones Flyway (V1–V4)
└── test/
    └── java/com/psa/proyecto_api/
        └── features/          # Pasos Cucumber + archivos .feature
```

### 3.3. Migraciones de Base de Datos

Las migraciones de Flyway construyen el esquema de forma incremental:

| Versión | Descripción |
| :--- | :--- |
| `V1` | Esquema de proyectos |
| `V2` | Esquema de tareas |
| `V3` | Esquema de tags |
| `V4` | Índices y triggers |

---

## 04. Testing

El proyecto incluye **16 escenarios BDD** implementados con Cucumber que cubren las historias de usuario del módulo:

| Historia de Usuario | Descripción |
| :--- | :--- |
| US-06 | Crear proyecto |
| US-07 | Planificar fechas de proyecto |
| US-08 | Etiquetar proyecto |
| US-09 | Ver proyectos |
| US-11 | Filtrar proyectos |
| US-16 | Ver detalle de proyecto |
| US-17 | Monitorear estado de proyecto |
| US-18 | Eliminar proyecto |
| US-19 | Crear tarea |
| US-20 | Asignar recurso a tarea |
| US-21 | Ver tareas |
| US-22 | Filtrar tareas |
| US-25 | Modificar horas estimadas |
| US-26 | Cambiar estado de tarea |
| US-27 | Ver detalle de tarea |
| US-31 | Calcular duración estimada del proyecto |

---

## 05. Quick Start

### Prerrequisitos

- Java 17 (JDK instalado y configurado en `PATH`)
- Maven (se puede usar el wrapper `./mvnw` incluido)
- Archivo `.env` con las credenciales de la base de datos (ver `.env.example`)

### Configuración Inicial

```bash
# 1. Clonar el repositorio
git clone https://github.com/Valentino-Carmona/PSA.git
cd PSA

# 2. Crear el archivo de entorno
cp .env.example .env
# Editá .env con tus credenciales de Neon (PostgreSQL)
```

Variables de entorno requeridas en `.env`:

```bash
# Base de datos PostgreSQL (Neon)
PGHOST=tu_host
PGDATABASE=tu_db
PGUSER=tu_usuario
PGPASSWORD=tu_contraseña

# JPA / Flyway
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_FLYWAY_ENABLED=true

# Servidor
PORT=8080
```

### Ejecución

```bash
# Inicia la aplicación en modo desarrollo (limpia, instala y ejecuta)
make dev
```

La API quedará disponible en `http://localhost:8080`.

### Comandos Disponibles

| Comando | Descripción |
| :--- | :--- |
| `make dev` | Limpia, instala dependencias e inicia la app |
| `make install` | Instala dependencias y compila |
| `make clean` | Limpia el proyecto |
| `make test` | Ejecuta los tests |
| `make setup` | Crea `.env` desde `.env.example` si no existe |
| `make build` | Construye la imagen Docker |
| `make up` | Levanta los servicios con Docker Compose |
| `make down` | Detiene los servicios |
| `make db-reset` | Resetea la base de datos (flyway clean + migrate) |
| `make flyway-info` | Muestra el estado de las migraciones |

---

## 06. API & Documentación

- **Swagger UI (producción):** [squad-07-2025-1c.onrender.com/swagger-ui/index.html](https://squad-07-2025-1c.onrender.com/swagger-ui/index.html)
- **Swagger UI (local):** `http://localhost:8080/swagger-ui/index.html`
- **Documentación final del proyecto:** [Google Drive](https://drive.google.com/file/d/1e22B1wmQyQSw3FzNEy5DdK-x8szU7Pth/view?usp=drive_link)

> [!IMPORTANT]
> El servicio corre en el tier gratuito de Render. La primera petición tras inactividad puede demorar varios segundos mientras el servidor se reinicia.

---

## Autores

Desarrollado colaborativamente por el equipo del Squad 07 — Ingeniería de Software, FIUBA (2025):

- **Valentino Carmona** — [github.com/Valentino-Carmona](https://github.com/Valentino-Carmona)
- Bruno Contreras
- Juan S. Burgos
- muribe
