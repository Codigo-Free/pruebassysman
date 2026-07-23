package com.sysman.ordenes.aplicacion.dto.resultado;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.Orden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;

import java.time.Instant;

public record OrdenResultado(
        Long id,
        Long idCliente,
        TipoOrden tipo,
        EstadoOrden estado,
        String descripcion,
        String direccionServicio,
        Long version,
        Instant fechaCreacion,
        Instant fechaModificacion,
        String usuarioCrea,
        String usuarioModifica
) {

    public static OrdenResultado desde(Orden orden) {
        return new OrdenResultado(
                orden.getId(),
                orden.getIdCliente(),
                orden.getTipo(),
                orden.getEstado(),
                orden.getDescripcion(),
                orden.getDireccionServicio(),
                orden.getVersion(),
                orden.getFechaCreacion(),
                orden.getFechaModificacion(),
                orden.getUsuarioCrea(),
                orden.getUsuarioModifica());
    }
}
