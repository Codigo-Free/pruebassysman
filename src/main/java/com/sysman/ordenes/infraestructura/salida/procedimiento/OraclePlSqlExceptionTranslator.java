package com.sysman.ordenes.infraestructura.salida.procedimiento;

import com.sysman.ordenes.dominio.excepcion.ConflictoVersionOrdenException;
import com.sysman.ordenes.dominio.excepcion.OrdenNoEncontradaException;
import com.sysman.ordenes.dominio.excepcion.TransicionEstadoInvalidaException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Traduce las excepciones personalizadas de {@code PKG_ORDENES.SP_ACTUALIZAR_ESTADO_ORDEN}
 * (ORA-20001/20002/20003) a excepciones de dominio, desacoplando la capa de aplicación
 * del código de error específico de Oracle.
 */
@Component
public class OraclePlSqlExceptionTranslator {

    private static final int ORA_ORDEN_NO_ENCONTRADA = 20001;
    private static final int ORA_TRANSICION_INVALIDA = 20002;
    private static final int ORA_CONFLICTO_VERSION = 20003;

    public RuntimeException traducir(DataAccessException excepcionOrigen, Long idOrden) {
        SQLException sqlException = extraerSqlException(excepcionOrigen);
        if (sqlException == null) {
            return excepcionOrigen;
        }

        int codigoOracle = Math.abs(sqlException.getErrorCode());
        return switch (codigoOracle) {
            case ORA_ORDEN_NO_ENCONTRADA -> new OrdenNoEncontradaException(idOrden);
            case ORA_CONFLICTO_VERSION -> new ConflictoVersionOrdenException(idOrden);
            case ORA_TRANSICION_INVALIDA -> new TransicionEstadoInvalidaException(sqlException.getMessage());
            default -> excepcionOrigen;
        };
    }

    private SQLException extraerSqlException(Throwable throwable) {
        Throwable actual = throwable;
        while (actual != null) {
            if (actual instanceof SQLException sqlException) {
                return sqlException;
            }
            actual = actual.getCause();
        }
        return null;
    }
}
