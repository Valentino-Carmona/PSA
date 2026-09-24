# Auditoría de Suite de Pruebas (Gestión de Proyectos PSA)

Este documento detalla la estructura y el alcance de los 313 tests desarrollados para la API de Gestión de Proyectos, así como el análisis de deuda técnica (placeholders, tests deshabilitados y métodos incompletos).

## 1. Desglose de Pruebas

La suite de pruebas se divide en dos categorías principales: Pruebas Unitarias (JUnit + Mockito) y Pruebas de Integración/BDD (Cucumber).

### Pruebas Unitarias (Capa de Modelo)
* **`ProjectTest`**: Verifica validaciones de creación, fechas inválidas (`endDate < startDate`), y comportamiento de ramas ante instancias nulas o IDs inexistentes en comparaciones.
* **`TaskTest`**: Valida estados iniciales (`TO_DO`), transiciones de estados (activar, completar), restricciones lógicas (no iniciar si no es `TO_DO`) y comportamientos límite.
* **`ProjectTagTest` / `TaskTagTest`**: Pruebas de robustez sobre colecciones de tags para evitar duplicados y controlar el `equals/hashCode` incluso con IDs nulos.
* **`EnumsTest`**: Valida que los conversores de string (usados en la web) para estados (`TaskStatus`, `ProjectStatus`) mapeen correctamente e invaliden entradas basura.

### Pruebas Unitarias (Seguridad y Control de Tráfico)
* **`JwtAuthenticationFilterTest`**: Comprueba extracciones correctas de token desde el Header, token inválido o malformado (sin prefijo Bearer), y delegación al contexto de Spring Security.
* **`RateLimitingFilterTest`**: Valida la capacidad de las cubetas de tokens (Bucket4j), permitiendo peticiones por debajo del límite IP y arrojando estado HTTP 429 cuando se excede.

### Pruebas Unitarias (Mappers y Especificaciones)
* **Mapeadores MapStruct**: Valida explícitamente los casos en donde los DTOs de request contienen atributos nulos para no fallar con `NullPointerExceptions`.
* **Filtros (Specification)**: Pruebas dinámicas de consultas a base de datos.

### Pruebas End-to-End / BDD (Cucumber)
Los 94 escenarios ejecutados bajo BDD (Archivos `.feature`) validan los "Criterios de Aceptación" de las Historias de Usuario:
* **US-11 Filtrar Proyectos**: Escenarios de búsqueda por nombre, tipo, estado y tags.
* **US-16 Ver Detalle de Proyecto**: Recuperación de atributos del proyecto y sub-entidades.
* **US-27 Ver Detalle de Tarea**: Visualización de la tarea y manejo de error (404) al buscar una inexistente.
* **US-31 Calcular Duración Estimada**: Validación matemática de horas estimadas al agregar, restar o modificar subtareas de un proyecto.
* ...entre otras operaciones core de negocio (creación, edición, eliminación).

---

## 2. Análisis de Deuda Técnica y "Placeholders"

Tras auditar íntegramente la suite bajo los siguientes criterios:
- Anotaciones `@Disabled` o `@Ignore`.
- Comentarios de código conteniendo `TODO`, `FIXME`, `XXX`.
- Métodos de prueba vacíos sin aserciones.
- Aserciones bloqueadas mediante comentarios (`// assertEquals...`).

### Resultado de la Auditoría:
**No se ha detectado deuda técnica.** 

La suite se encuentra en un estado **100% puro y funcional**. 
- No hay ningún test "placeholder".
- No hay tests deshabilitados para evitar que rompan la build en CI.
- No hay recordatorios de implementación futura en los archivos de test.
- Todos los métodos (tanto JUnit como BDD) contienen un ciclo Arrange-Act-Assert válido y con código funcional.

Esto concluye en que la arquitectura de pruebas cumple con estándares senior: ningún test ha sido abandonado a medias.
