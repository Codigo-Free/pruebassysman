package com.sysman.ordenes.aplicacion.dto.comando;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;

public record ActualizarEstadoComando(
        Long idOrden,
        EstadoOrden estadoNuevo,
        Long versionEsperada,
        String usuarioModifica,
        String observacion
) {
}
