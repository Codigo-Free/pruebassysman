# Sistema de Gestión de Órdenes Operativas

[![CI](https://github.com/Codigo-Free/pruebassysman/actions/workflows/ci.yml/badge.svg)](https://github.com/Codigo-Free/pruebassysman/actions/workflows/ci.yml)

API REST para la gestión de órdenes operativas de una empresa de servicios públicos: creación, consulta, actualización de estado con control de concurrencia, y listado con filtros. Construida con **Spring Boot 4**, **arquitectura hexagonal**, **DDD** y **Oracle** (persistencia + procedimiento PL/SQL para la transición de estados).

## Documentación

Toda la documentación de arquitectura, decisiones técnicas, modelo de datos y guía de uso está en [`docs/`](docs/):

- [`docs/propuesta-arquitectura.md`](docs/propuesta-arquitectura.md) — arquitectura, justificación técnica, transacciones, concurrencia, indexación, versionado de API, logging y auditoría.
- [`docs/arquitectura.md`](docs/arquitectura.md) — capas hexagonales y diagrama.
- [`docs/decisiones-tecnicas.md`](docs/decisiones-tecnicas.md) — ADRs.
- [`docs/modelo-datos.md`](docs/modelo-datos.md) — modelo Oracle, índices, particionamiento.
- [`docs/api.md`](docs/api.md) — contrato de la API.
- [`docs/guia-setup.md`](docs/guia-setup.md) — cómo levantar el proyecto localmente.
- [`docs/preguntas-tecnicas.md`](docs/preguntas-tecnicas.md) y [`docs/preguntas-cargo.md`](docs/preguntas-cargo.md) — respuestas a las preguntas de la prueba técnica.

## Quickstart

```bash
# 1. Levantar Oracle localmente
docker compose up -d

# 2. Compilar y correr pruebas unitarias
mvn verify

# 3. Ejecutar la aplicación (aplica migraciones Flyway automáticamente)
mvn spring-boot:run

# 4. Documentación interactiva de la API
open http://localhost:8080/swagger-ui.html
```

Pruebas de integración contra Oracle real (Testcontainers):

```bash
mvn verify -Pintegration
```

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/v1/orden` | Crear una orden |
| `GET` | `/api/v1/orden/{id}` | Consultar una orden |
| `PUT` | `/api/v1/orden/{id}/estado` | Actualizar el estado de una orden |
| `GET` | `/api/v1/orden?estado=&fechaInicio=&fechaFin=` | Listar órdenes con paginación y filtros |

## Stack técnico

Spring Boot 4.1 · Spring Framework 7 · Java 21 · Spring Data JPA · Oracle (JDBC + PL/SQL) · Flyway · springdoc-openapi · MapStruct · JUnit 5 · Mockito · ArchUnit · Testcontainers · JaCoCo.
