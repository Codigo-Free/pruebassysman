package com.sysman.ordenes.aplicacion.dto.comando;

import com.sysman.ordenes.dominio.modelo.TipoOrden;

public record CrearOrdenComando(
        Long idCliente,
        TipoOrden tipo,
        String descripcion,
        String direccionServicio,
        String usuarioCrea
) {
}
