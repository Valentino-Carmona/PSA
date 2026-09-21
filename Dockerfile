# Etapa 1: Builder
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Optimización de caché: copiar primero solo archivos de dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Dar permisos al wrapper y descargar dependencias (se cachea si pom.xml no cambia)
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN ./mvnw clean package -DskipTests && \
    cp target/*.jar app.jar

# Etapa 2: Producción (Runtime)
FROM eclipse-temurin:17-jre-alpine AS runtime

# Hardening: Crear grupo y usuario no-root
RUN addgroup -g 1001 -S appgroup && \
    adduser -S appuser -u 1001 -G appgroup

WORKDIR /app

# Copiar solo el artefacto final desde el builder y asignar ownership
COPY --from=builder --chown=appuser:appgroup /app/app.jar ./app.jar

# Cambiar al usuario no-root
USER 1001

ENV PORT=8080
EXPOSE ${PORT}

# Healthcheck nativo: verifica que la UI de Swagger responda (garantizando que la API levantó)
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --spider -q http://localhost:${PORT}/swagger-ui/index.html || exit 1

# Comando de ejecución
CMD ["java", "-jar", "app.jar"]