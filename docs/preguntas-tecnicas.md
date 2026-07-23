# Preguntas técnicas (prueba técnica)

## 1. ¿Cómo optimizarías una consulta que tarda 12 segundos con 10 millones de registros?

Enfoque en orden:

1. **`EXPLAIN PLAN` / `DBMS_XPLAN`** primero, no adivinar. Buscar `TABLE ACCESS FULL` sobre la tabla grande, `NESTED LOOPS` con muchas iteraciones, o un `CARDINALITY` estimado muy distinto del real (estadísticas desactualizadas — revisar con `DBMS_STATS.GATHER_TABLE_STATS` si las estimaciones no cuadran).
2. **Índice(s) que cubran el filtro real** de la consulta (ver pregunta 2) — en este proyecto, `IDX_ORDEN_ESTADO_FECHA (ESTADO, FECHA_CREACION)` existe justamente porque el filtro típico es por esas dos columnas juntas; un índice solo en `ESTADO` no ayudaría si la consulta también filtra por rango de fechas.
3. **Partition pruning:** si la tabla está particionada por fecha (como `ORDEN`) y la consulta filtra por rango de fechas, confirmar en el plan que Oracle solo escanea las particiones relevantes (`Pstart`/`Pstop` en el plan), no toda la tabla.
4. **Evitar funciones sobre la columna indexada** en el `WHERE` (`TRUNC(FECHA_CREACION) = ...` invalida el índice normal; hay que indexar la expresión o reescribir el filtro como rango).
5. **Revisar el `SELECT *`**: traer solo las columnas necesarias reduce I/O, y si son pocas puede lograrse un *index-only scan* (todas las columnas pedidas están en el índice, sin tocar la tabla).
6. Si sigue lento: `PARALLEL` en la consulta (con cuidado en OLTP, más pensado para reportes/batch), o revisar si hace falta un índice compuesto adicional para un patrón de consulta específico que se repite mucho.

## 2. ¿Qué tipo de índices usarías?

- **B-Tree único** para PKs y para columnas con restricción de unicidad de negocio (`CLIENTE.DOCUMENTO`).
- **B-Tree normal** sobre toda FK que participe en joins o en `WHERE` frecuentes, para evitar bloqueos de tabla completa en updates/deletes de la tabla padre y acelerar los joins.
- **B-Tree compuesto** cuando el patrón de consulta filtra por varias columnas a la vez (`ESTADO` + `FECHA_CREACION` en este proyecto) — el orden de las columnas en el índice debe seguir la regla de "igualdad primero, rango después" (`ESTADO =` seguido de `FECHA_CREACION BETWEEN`).
- **Bitmap** solo consideraría en un escenario de solo lectura/reporting con columnas de baja cardinalidad (ej. un `TIPO_ORDEN` con pocos valores distintos) — **no** en `ORDEN`, porque es una tabla OLTP con updates frecuentes y los índices bitmap degradan mucho la concurrencia de escritura (bloquean rangos amplios de filas).
- **Function-based** si un filtro habitual necesita una expresión (`UPPER(campo)`, `TRUNC(fecha)`) que de otro modo invalidaría un índice normal.

## 3. ¿Particionarías la tabla? ¿Cómo?

Sí — así se hizo con `ORDEN` y `ORDEN_HISTORICO` en este proyecto: **Range Partitioning por intervalo mensual** (`PARTITION BY RANGE (FECHA_CREACION) INTERVAL (NUMTOYMINTERVAL(1,'MONTH'))`), que crea automáticamente una partición nueva cada mes sin DDL manual. Justificación con 1M órdenes/mes:

- Las consultas más comunes filtran por un rango de fechas reciente → *partition pruning* reduce el volumen escaneado a 1-2 particiones en vez de toda la tabla.
- El mantenimiento (purgar o archivar órdenes de hace 2+ años) se vuelve un `DROP PARTITION`/`EXCHANGE PARTITION` casi instantáneo, en vez de un `DELETE` masivo que genera toneladas de `UNDO`/`REDO`.
- Trade-off aceptado: la PK no está alineada con la columna de partición, así que su índice queda **global** (no local) — un `DROP PARTITION` lo deja `UNUSABLE` hasta reconstruirlo. Se documenta como decisión consciente en el ADR-006 del proyecto (ver `docs/decisiones-tecnicas.md`) en vez de forzar una PK compuesta solo para tener índices locales.

Si el volumen de lectura por cliente también creciera mucho, evaluaría una segunda dimensión con **subparticiones** (composite range-hash por `ID_CLIENTE`), pero no lo justificaría desde el día uno sin evidencia de ese patrón de acceso.

## 4. ¿Cómo manejarías concurrencia y transacciones?

- **Optimistic locking** como default para entidades con updates concurrentes poco frecuentes por fila (es el caso de una orden individual): columna `VERSION`, verificada en el `UPDATE ... WHERE id=? AND version=?`; si `ROWCOUNT=0`, se asume conflicto y se responde 409, dejando que el cliente decida si reintenta. Evita mantener locks abiertos mientras el usuario "piensa" entre leer y escribir (a diferencia del locking pesimista).
- **Pesimista (`SELECT ... FOR UPDATE`)** solo en el tramo estrictamente necesario dentro de una transacción corta — así se usa dentro de `SP_ACTUALIZAR_ESTADO_ORDEN`: bloquea la fila justo antes de validar y actualizar, y libera el lock al hacer commit inmediatamente después, minimizando el tiempo que otra transacción queda esperando.
- **Transacciones cortas y explícitas:** cada caso de uso que escribe decide conscientemente su alcance transaccional (`@Transactional` de Spring para la creación; una transacción PL/SQL autocontenida para la actualización de estado) en vez de una transacción larga que abarque llamadas HTTP externas o lógica no relacionada.
- **Nunca mezclar dos gestores de transacciones sobre la misma operación** (Spring/JPA y una transacción PL/SQL con su propio commit) — de ahí que `ActualizarEstadoOrdenService` no tenga `@Transactional`.
- A nivel de aislamiento, `READ_COMMITTED` (default de Oracle) es suficiente para este caso de uso porque la única invariante crítica (la versión no cambió) ya se protege explícitamente con el `WHERE version=?`, no dependiendo del nivel de aislamiento de la transacción.

## 5. ¿Cómo aseguras calidad técnica en el equipo?

*(Nota: esta pregunta también aparece en el bloque de liderazgo del PDF — la respuesta aquí se centra en las prácticas técnicas concretas; ver [`preguntas-cargo.md`](preguntas-cargo.md) para la dimensión de gestión de equipo.)*

- **Pruebas automatizadas en cada capa con la herramienta correcta**: dominio con JUnit puro (sin framework), aplicación con mocks sobre los puertos, adaptadores con slice tests (`@DataJpaTest`/`@WebMvcTest`), y al menos un test de integración end-to-end contra la infraestructura real (Testcontainers) que valide que las piezas realmente encajan, no solo que cada una funciona aislada.
- **Un test de arquitectura (ArchUnit)** que falla el build si alguien rompe el límite entre capas — convierte una regla de diseño en algo que se aplica automáticamente, no que depende de que el reviewer se acuerde de revisarlo.
- **CI obligatorio antes de mergear** (build + tests en cada PR), con branch protection para que no sea opcional.
- **Code review real, no de trámite**: enfocado en que el cambio resuelva el problema con el menor acoplamiento posible y sin introducir deuda oculta, no en preferencias de estilo (para eso está el formateo automático).
- **Documentación de las decisiones no obvias** (ADRs cortos) para que la razón detrás de una decisión sobreviva a que la persona que la tomó ya no esté en el proyecto.
