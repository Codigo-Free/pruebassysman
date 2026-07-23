-- B-Tree normal sobre la FK, evita bloqueos de tabla completa en operaciones sobre CLIENTE.
CREATE INDEX IDX_ORDEN_CLIENTE ON ORDEN (ID_CLIENTE);

-- Índice compuesto exigido para optimizar GET /orden?estado=&fechaInicio=&fechaFin=.
CREATE INDEX IDX_ORDEN_ESTADO_FECHA ON ORDEN (ESTADO, FECHA_CREACION);

-- B-Tree normal sobre la FK de ORDEN_HISTORICO.
CREATE INDEX IDX_HIST_ORDEN ON ORDEN_HISTORICO (ID_ORDEN);
