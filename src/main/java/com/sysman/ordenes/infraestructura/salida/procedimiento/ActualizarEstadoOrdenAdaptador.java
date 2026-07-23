package com.sysman.ordenes.infraestructura.salida.procedimiento;

import com.sysman.ordenes.aplicacion.puerto.salida.ActualizarEstadoOrdenPuerto;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Types;

/**
 * Invoca {@code PKG_ORDENES.SP_ACTUALIZAR_ESTADO_ORDEN} vía {@link SimpleJdbcCall}
 * (en vez de {@code @NamedStoredProcedureQuery}) para que Spring traduzca las excepciones
 * SQL de forma desacoplada de JPA. La atomicidad de la actualización + auditoría queda
 * delegada por completo al procedimiento; esta clase nunca abre una transacción Spring.
 */
@Component
public class ActualizarEstadoOrdenAdaptador implements ActualizarEstadoOrdenPuerto {

    private final SimpleJdbcCall llamadaProcedimiento;
    private final OraclePlSqlExceptionTranslator traductor;

    public ActualizarEstadoOrdenAdaptador(DataSource dataSource, OraclePlSqlExceptionTranslator traductor) {
        this.traductor = traductor;
        this.llamadaProcedimiento = new SimpleJdbcCall(dataSource)
                .withCatalogName("PKG_ORDENES")
                .withProcedureName("SP_ACTUALIZAR_ESTADO_ORDEN")
                .declareParameters(
                        new SqlParameter("P_ID_ORDEN", Types.NUMERIC),
                        new SqlParameter("P_ESTADO_NUEVO", Types.VARCHAR),
                        new SqlParameter("P_VERSION_ESPERADA", Types.NUMERIC),
                        new SqlParameter("P_USUARIO_MODIFICA", Types.VARCHAR),
                        new SqlParameter("P_OBSERVACION", Types.VARCHAR));
    }

    @Override
    public void actualizarEstado(Long idOrden, EstadoOrden estadoNuevo, Long versionEsperada,
                                  String usuarioModifica, String observacion) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("P_ID_ORDEN", idOrden)
                .addValue("P_ESTADO_NUEVO", estadoNuevo.name())
                .addValue("P_VERSION_ESPERADA", versionEsperada)
                .addValue("P_USUARIO_MODIFICA", usuarioModifica)
                .addValue("P_OBSERVACION", observacion);

        try {
            llamadaProcedimiento.execute(parametros);
        } catch (DataAccessException excepcion) {
            throw traductor.traducir(excepcion, idOrden);
        }
    }
}
