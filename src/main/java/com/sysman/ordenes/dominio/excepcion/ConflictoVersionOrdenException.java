package com.sysman.ordenes.dominio.excepcion;

/** Se lanza cuando otro proceso modificó la orden entre la lectura y la escritura (optimistic locking). */
public class ConflictoVersionOrdenException extends RuntimeException {

    public ConflictoVersionOrdenException(Long idOrden) {
        super("La orden " + idOrden + " fue modificada por otro proceso; consulte el estado actual e intente nuevamente");
    }
}
