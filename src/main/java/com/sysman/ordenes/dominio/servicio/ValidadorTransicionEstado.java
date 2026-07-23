package com.sysman.ordenes.dominio.servicio;

import com.sysman.ordenes.dominio.excepcion.TransicionEstadoInvalidaException;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;

/**
 * Encapsula la política de validación de transición de estados como una estrategia
 * independiente de {@link com.sysman.ordenes.dominio.modelo.Orden}, de modo que pueda
 * sustituirse (por ejemplo, por una variante que permita overrides administrativos)
 * sin tocar el agregado.
 */
public class ValidadorTransicionEstado {

    public void validar(EstadoOrden estadoActual, EstadoOrden estadoDestino) {
        if (!estadoActual.puedeTransicionarA(estadoDestino)) {
            throw new TransicionEstadoInvalidaException(estadoActual, estadoDestino);
        }
    }
}
