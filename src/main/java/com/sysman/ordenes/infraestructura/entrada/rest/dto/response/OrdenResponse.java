package com.sysman.ordenes.infraestructura.entrada.rest.dto.response;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;

import java.time.Instant;

public record OrdenResponse(
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
}
