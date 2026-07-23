package com.sysman.ordenes.dominio.excepcion;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long idCliente) {
        super("No existe un cliente con id " + idCliente);
    }
}
