-- Paquete PKG_ORDENES: concentra la lógica de actualización de estado de una orden.
-- Responsabilidades (delegadas intencionalmente a la base de datos, ver docs/decisiones-tecnicas.md):
--   1) Validar existencia de la orden.
--   2) Validar la transición de estado contra ORDEN_TRANSICION_VALIDA (autoridad final).
--   3) Aplicar optimistic locking comparando la versión esperada.
--   4) Insertar el registro de auditoría en ORDEN_HISTORICO.
-- Todo dentro de una única transacción atómica, con excepciones personalizadas
-- traducidas por com.sysman.ordenes.infraestructura.salida.procedimiento.OraclePlSqlExceptionTranslator.
CREATE OR REPLACE PACKAGE PKG_ORDENES AS

    EX_ORDEN_NO_ENCONTRADA EXCEPTION;
    PRAGMA EXCEPTION_INIT(EX_ORDEN_NO_ENCONTRADA, -20001);

    EX_TRANSICION_INVALIDA EXCEPTION;
    PRAGMA EXCEPTION_INIT(EX_TRANSICION_INVALIDA, -20002);

    EX_CONFLICTO_VERSION EXCEPTION;
    PRAGMA EXCEPTION_INIT(EX_CONFLICTO_VERSION, -20003);

    PROCEDURE SP_ACTUALIZAR_ESTADO_ORDEN(
        p_id_orden          IN NUMBER,
        p_estado_nuevo      IN VARCHAR2,
        p_version_esperada  IN NUMBER,
        p_usuario_modifica  IN VARCHAR2,
        p_observacion       IN VARCHAR2 DEFAULT NULL
    );

END PKG_ORDENES;
/

CREATE OR REPLACE PACKAGE BODY PKG_ORDENES AS

    PROCEDURE SP_ACTUALIZAR_ESTADO_ORDEN(
        p_id_orden          IN NUMBER,
        p_estado_nuevo      IN VARCHAR2,
        p_version_esperada  IN NUMBER,
        p_usuario_modifica  IN VARCHAR2,
        p_observacion       IN VARCHAR2 DEFAULT NULL
    ) IS
        v_estado_actual      ORDEN.ESTADO%TYPE;
        v_version_actual     ORDEN.VERSION%TYPE;
        v_transiciones_ok    PLS_INTEGER;
        v_filas_actualizadas PLS_INTEGER;
    BEGIN
        BEGIN
            SELECT ESTADO, VERSION
              INTO v_estado_actual, v_version_actual
              FROM ORDEN
             WHERE ID_ORDEN = p_id_orden
             FOR UPDATE;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20001, 'ORDEN_NO_ENCONTRADA: no existe la orden ' || p_id_orden);
        END;

        SELECT COUNT(*)
          INTO v_transiciones_ok
          FROM ORDEN_TRANSICION_VALIDA
         WHERE ESTADO_ORIGEN = v_estado_actual
           AND ESTADO_DESTINO = p_estado_nuevo;

        IF v_transiciones_ok = 0 THEN
            RAISE_APPLICATION_ERROR(-20002,
                'TRANSICION_INVALIDA: no se puede pasar de ' || v_estado_actual || ' a ' || p_estado_nuevo);
        END IF;

        UPDATE ORDEN
           SET ESTADO             = p_estado_nuevo,
               VERSION            = VERSION + 1,
               FECHA_MODIFICACION = SYSTIMESTAMP,
               USUARIO_MODIFICA   = p_usuario_modifica
         WHERE ID_ORDEN = p_id_orden
           AND VERSION = p_version_esperada;

        v_filas_actualizadas := SQL%ROWCOUNT;

        IF v_filas_actualizadas = 0 THEN
            RAISE_APPLICATION_ERROR(-20003,
                'CONFLICTO_VERSION: la orden ' || p_id_orden || ' fue modificada por otro proceso');
        END IF;

        INSERT INTO ORDEN_HISTORICO (
            ID_ORDEN, ESTADO_ANTERIOR, ESTADO_NUEVO, USUARIO_MODIFICA, FECHA_MODIFICACION, OBSERVACION
        ) VALUES (
            p_id_orden, v_estado_actual, p_estado_nuevo, p_usuario_modifica, SYSTIMESTAMP, p_observacion
        );

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_ACTUALIZAR_ESTADO_ORDEN;

END PKG_ORDENES;
/
