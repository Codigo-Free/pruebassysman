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

    /**
     * Usado cuando la transición inválida es detectada por el procedimiento PL/SQL
     * (autoridad final) en lugar de la validación fail-fast en memoria, y por lo tanto
     * solo se dispone del mensaje textual, no de los estados tipados.
     */
    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
        this.estadoActual = null;
        this.estadoDestino = null;
    }

    public EstadoOrden getEstadoActual() {
        return estadoActual;
    }

    public EstadoOrden getEstadoDestino() {
        return estadoDestino;
    }
}
