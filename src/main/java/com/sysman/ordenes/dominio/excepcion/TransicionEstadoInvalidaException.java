package com.sysman.ordenes.dominio.excepcion;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;

public class TransicionEstadoInvalidaException extends RuntimeException {

    private final EstadoOrden estadoActual;
    private final EstadoOrden estadoDestino;

    public TransicionEstadoInvalidaException(EstadoOrden estadoActual, EstadoOrden estadoDestino) {
        super("No se puede transicionar de %s a %s".formatted(estadoActual, estadoDestino));
        this.estadoActual = estadoActual;
        this.estadoDestino = estadoDestino;
    }

    public EstadoOrden getEstadoActual() {
        return estadoActual;
    }

    public EstadoOrden getEstadoDestino() {
        return estadoDestino;
    }
}
