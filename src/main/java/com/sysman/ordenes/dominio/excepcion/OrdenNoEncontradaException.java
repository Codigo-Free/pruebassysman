package com.sysman.ordenes.dominio.excepcion;

public class OrdenNoEncontradaException extends RuntimeException {

    public OrdenNoEncontradaException(Long idOrden) {
        super("No existe una orden con id " + idOrden);
    }
}
