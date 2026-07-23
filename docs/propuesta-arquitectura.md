# Propuesta de Arquitectura — Sistema de Gestión de Órdenes Operativas

> Versión final, ajustada tras la implementación. La propuesta original (entregable de 2-3 páginas de la prueba técnica) se mantiene íntegra en su estructura; aquí se documenta cómo quedó efectivamente construida cada decisión.

## 1. Arquitectura propuesta

Arquitectura **Hexagonal (Puertos y Adaptadores)** sobre un monolito modular con Spring Boot 4, organizada en tres capas (ver [`arquitectura.md`](arquitectura.md) para el detalle de paquetes):

- **Dominio** (`dominio`): `Orden` y `Cliente` (aggregate roots), `EstadoOrden`/`TipoOrden` (value objects), `OrdenHistorico` (entidad inmutable de auditoría). Sin dependencias externas — verificado automáticamente con un test ArchUnit.
- **Aplicación** (`aplicacion`): puertos de entrada (casos de uso: crear, consultar, actualizar estado, listar) y puertos de salida (repositorios, invocación del procedimiento PL/SQL), con DTOs de comando/resultado propios.
- **Infraestructura** (`infraestructura`): adaptador REST (`OrdenController`), adaptadores de persistencia JPA, adaptador JDBC hacia el procedimiento PL/SQL, manejo global de excepciones y logging transversal.

## 2. Justificación técnica

Aislar el dominio de la infraestructura permite escalar o sustituir la capa de exposición (API) o el acceso a datos sin tocar las reglas de negocio, algo relevante dado el volumen esperado (1 millón de órdenes/mes). La inyección de dependencias vía puertos habilita pruebas unitarias con mocks en cada capa (dominio: JUnit puro; aplicación: Mockito sobre los puertos; adaptadores: `@DataJpaTest`/`@WebMvcTest`), sin necesidad de una base de datos real salvo en la suite de integración.

## 3. Estrategia de manejo de transacciones

Dos mecanismos complementarios, tal como se definió en la propuesta original:

- **A nivel de aplicación (Spring):** `CrearOrdenService.crear()` está anotado `@Transactional` — si falla la validación del cliente o la inserción, se hace rollback automático.
- **A nivel de base de datos (PL/SQL):** la actualización de estado y la inserción en `ORDEN_HISTORICO` se delegan por completo al paquete `PKG_ORDENES.SP_ACTUALIZAR_ESTADO_ORDEN`, invocado vía `SimpleJdbcCall` **sin** una transacción Spring envolvente. Esto evita mezclar dos gestores de transacciones (Spring/JPA y la transacción implícita del bloque PL/SQL) sobre la misma operación, y garantiza que la actualización del estado y el registro de auditoría sean atómicos incluso si el procedimiento se invoca desde otro lugar (batch, otro servicio) en el futuro.

## 4. Manejo de concurrencia

Bloqueo optimista con doble verificación:

- La entidad `OrdenJpaEntity` tiene `@Version` (columna `VERSION`), usado en las lecturas (`GET /orden/{id}`, listado) para exponer la versión vigente al cliente.
- La verificación real de concurrencia ocurre **dentro del procedimiento PL/SQL**: `UPDATE ORDEN SET ... WHERE ID_ORDEN=? AND VERSION=?`; si `SQL%ROWCOUNT=0` (otro proceso ya modificó la orden), se lanza `ORA-20003`, traducido a `ConflictoVersionOrdenException` y mapeado a **HTTP 409 Conflict**. Esto reemplaza el `OptimisticLockException` de JPA porque la actualización de estado nunca pasa por un `save()` de JPA — ver ADR correspondiente en [`decisiones-tecnicas.md`](decisiones-tecnicas.md).

## 5. Estrategia de indexación en Oracle

- **B-Tree únicos:** PK de `CLIENTE`, `ORDEN` y `ORDEN_HISTORICO` (`ID_CLIENTE`, `ID_ORDEN`, `ID_HISTORICO`).
- **B-Tree normales:** sobre las FK (`ORDEN.ID_CLIENTE`, `ORDEN_HISTORICO.ID_ORDEN`), evitando bloqueos de tabla completa en operaciones sobre las tablas maestras.
- **Índice compuesto:** `IDX_ORDEN_ESTADO_FECHA (ESTADO, FECHA_CREACION)`, que soporta directamente el filtro de `GET /orden?estado=&fechaInicio=&fechaFin=`.
- **Particionamiento:** `ORDEN` y `ORDEN_HISTORICO` particionadas por `RANGE INTERVAL` mensual sobre su columna de fecha, para que el volumen mensual (1M órdenes) quede aislado por partición y las lecturas históricas no degraden las consultas recientes.

## 6. Estrategia de versionamiento de API

Versionado por URI: todos los endpoints viven bajo `/api/v1/orden`. Documentado automáticamente con springdoc-openapi (`/swagger-ui.html`, `/api-docs`).

## 7. Estrategia de logging y auditoría

- **Logging:** logs estructurados en JSON (logback + `logstash-logback-encoder`). `CorrelationIdFilter` genera o propaga un `X-Correlation-Id` por petición HTTP y lo publica en el MDC, de modo que aparece en cada línea de log de esa petición (incluidos los errores capturados por `GlobalExceptionHandler`, que lo devuelve también en el cuerpo del `ProblemDetail`).
- **Auditoría de datos:** `ORDEN_HISTORICO` se llena exclusivamente desde `SP_ACTUALIZAR_ESTADO_ORDEN`, con `usuario_modifica`, `fecha_modificacion`, `estado_anterior` y `estado_nuevo` inmutables — nunca se escribe desde la capa de aplicación Java, evitando que quede desincronizada del estado real de la orden.
