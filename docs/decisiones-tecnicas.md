# Decisiones técnicas (ADRs)

Formato corto: contexto → decisión → consecuencias.

## ADR-001 — Mono-módulo Maven en lugar de multi-módulo

**Contexto:** la arquitectura hexagonal suele reforzarse con módulos Maven separados por capa.
**Decisión:** un único módulo Maven, con la separación aplicada a nivel de paquetes y verificada con ArchUnit.
**Consecuencias:** build más simple (un solo `pom.xml`, un solo artefacto Spring Boot) sin perder la garantía de aislamiento arquitectónico. Si el proyecto creciera a varios bounded contexts, la alternativa multi-módulo seguiría siendo válida.

## ADR-002 — Java 21 (LTS) como target de compilación, aunque el JDK instalado es 26

**Contexto:** el entorno de desarrollo tiene JDK 26 (release no-LTS más reciente); Spring Boot 4.1 soporta Java 17+ y certifica hasta Java 26, pero el ecosistema de librerías (MapStruct, Lombok, ArchUnit, Testcontainers) recomienda una base LTS.
**Decisión:** `<maven.compiler.release>21</maven.compiler.release>`, compilando con el JDK 26 instalado localmente; CI fija explícitamente Java 21 (Temurin) con `actions/setup-java` para reproducibilidad independiente del JDK del desarrollador.
**Consecuencias:** máxima compatibilidad de librerías y builds reproducibles en CI, sin perder la posibilidad de ejecutar/depurar localmente con un JDK más nuevo.

## ADR-003 — `SimpleJdbcCall` en lugar de `@NamedStoredProcedureQuery` para invocar el PL/SQL

**Contexto:** `SP_ACTUALIZAR_ESTADO_ORDEN` debe invocarse desde Spring y sus excepciones personalizadas (ORA-20001/20002/20003) deben traducirse a excepciones de dominio.
**Decisión:** `SimpleJdbcCall` sobre `JdbcTemplate`, no `@NamedStoredProcedureQuery` de JPA.
**Consecuencias:** Spring traduce `SQLException` a la jerarquía `DataAccessException` de forma desacoplada del proveedor JPA (Hibernate), lo que simplifica `OraclePlSqlExceptionTranslator` y facilita mockear `JdbcTemplate`/`SimpleJdbcCall` en tests. La alternativa con JPA ataría el manejo de errores al `EntityManager` y a los mensajes específicos de Hibernate.

## ADR-004 — El procedimiento PL/SQL es la autoridad final de la transición de estados

**Contexto:** la validación de transición de `EstadoOrden` existe tanto en Java (`EstadoOrden.puedeTransicionarA`) como en la tabla `ORDEN_TRANSICION_VALIDA` consultada por el procedimiento.
**Decisión:** el PL/SQL es la autoridad final; la validación en Java es solo fail-fast (evita un round-trip a la base de datos para transiciones obviamente inválidas y permite exponer errores más rápido). Si ambas llegaran a divergir, el procedimiento decide.
**Consecuencias:** riesgo de duplicación/drift entre la matriz en `EstadoOrden` y la tabla `ORDEN_TRANSICION_VALIDA` — mitigado documentando ambas listas una junto a la otra (ver [`modelo-datos.md`](modelo-datos.md)) y con un test de contrato (`EstadoOrdenTest`) que fija explícitamente la matriz esperada.

## ADR-005 — Sin `@Transactional` en `ActualizarEstadoOrdenService`

**Contexto:** la actualización de estado + inserción en histórico ya es atómica dentro del procedimiento PL/SQL (COMMIT/ROLLBACK explícitos en el propio bloque).
**Decisión:** el servicio de aplicación no abre una transacción Spring adicional para esta operación.
**Consecuencias:** evita mezclar dos gestores de transacciones sobre la misma operación (el commit del PL/SQL sería prematuro si además hubiera una transacción Spring envolvente). Como contraparte, este método **nunca** debe extenderse para incluir otras escrituras JPA sin revisar esta decisión primero.

## ADR-006 — Particionamiento con índice global en la PK (no local)

**Contexto:** `ORDEN` y `ORDEN_HISTORICO` están particionadas por rango mensual sobre su columna de fecha, pero la PK (`ID_ORDEN`, `ID_HISTORICO`) no es la columna de partición.
**Decisión:** se acepta el índice único **global** por defecto que Oracle crea para la PK en este escenario.
**Consecuencias:** un `DROP PARTITION` futuro (purga de datos antiguos) dejaría ese índice `UNUSABLE` hasta un `ALTER INDEX ... REBUILD` (o usar `UPDATE INDEXES` en el DDL de mantenimiento). Se documenta como trade-off aceptado: preferible a complicar la aplicación con una PK compuesta que incluya la fecha solo para habilitar un índice local.

## ADR-007 — `TIMESTAMP WITH LOCAL TIME ZONE` en lugar de `TIMESTAMP`

**Contexto:** las columnas de auditoría (`FECHA_CREACION`, `FECHA_MODIFICACION`) se mapean a `java.time.Instant` en las entidades JPA.
**Decisión:** se usa `TIMESTAMP WITH LOCAL TIME ZONE` en las migraciones Flyway, no `TIMESTAMP` a secas.
**Consecuencias:** un `TIMESTAMP` sin zona horaria no puede mapearse directamente a `Instant` vía el driver `ojdbc11` (produce `ORA-18716` en tiempo de ejecución, detectado al probar contra Oracle real). `WITH LOCAL TIME ZONE` normaliza a UTC internamente y resuelve el mapeo sin ambigüedad. Detalle adicional: el límite de partición debe expresarse como literal `WITH TIME ZONE` (`ORA-30078` si no), no como `TIMESTAMP` simple.

## ADR-008 — Testcontainers para integración, no bloqueante en el pipeline principal

**Contexto:** el test de integración end-to-end (`OrdenApiIntegrationTest`) arranca un contenedor Oracle real (`gvenzl/oracle-free`), lo que toma 30-90 segundos incluso con la imagen ya descargada.
**Decisión:** separado en el profile Maven `integration` (`mvn verify -Pintegration`) y en un workflow de GitHub Actions aparte (`ci-integration.yml`), disparado manualmente o cada noche — no en cada push/PR.
**Consecuencias:** el feedback de cada PR (`ci.yml`) permanece rápido (~1 minuto); el costo es que un PR puede quedar verde sin haber corrido el test contra Oracle real, mitigado por la ejecución nocturna programada.

## ADR-009 — `main` y `develop` sincronizados en cada fase, no solo al cierre

**Contexto:** el plan original consideraba que `main` solo recibiera el merge final (`v1.0.0`) al cerrar el proyecto.
**Decisión:** tras un evento inesperado en el que `main` quedó sincronizado con `develop` tempranamente (sin romper nada), se decidió con el usuario adoptar ese modelo de forma consciente: sincronizar `main` con `develop` al cierre de cada fase, en lugar de esperar al final.
**Consecuencias:** `main` queda siempre desplegable con lo último integrado; se pierde la propiedad de "main solo se mueve una vez al final", que en un proyecto de un solo desarrollador no aportaba beneficio real frente a la simplicidad de mantener ambas ramas alineadas.

## ADR-010 — Mapeo dominio↔persistencia manual, no con MapStruct

**Contexto:** `Orden` y `Cliente` son inmutables y se construyen mediante métodos de fábrica estáticos (`crear`, `reconstruir`), no mediante un constructor único ni setters.
**Decisión:** `OrdenPersistenciaMapper` y `ClientePersistenciaMapper` son clases `@Component` escritas a mano; MapStruct se reserva para `OrdenRestMapper`, donde el mapeo es campo-a-campo entre DTOs/records simples.
**Consecuencias:** se evita forzar a MapStruct a generar código para un patrón (fábricas estáticas) que no soporta bien de forma nativa, a cambio de un pequeño mapper manual explícito y fácil de leer.
