package com.sysman.ordenes.aplicacion.dto.comando;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;

import java.time.Instant;

public record ListarOrdenesComando(
        EstadoOrden estado,
        Instant fechaInicio,
        Instant fechaFin,
        int pagina,
        int tamanoPagina
) {
}
