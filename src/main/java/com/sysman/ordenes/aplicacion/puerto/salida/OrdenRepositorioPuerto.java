package com.sysman.ordenes.aplicacion.puerto.salida;

import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.Orden;

import java.time.Instant;
import java.util.Optional;

public interface OrdenRepositorioPuerto {

    Orden guardar(Orden orden);

    Optional<Orden> buscarPorId(Long id);

    PaginaResultado<Orden> buscar(EstadoOrden estado, Instant fechaInicio, Instant fechaFin,
                                   int pagina, int tamanoPagina);
}
