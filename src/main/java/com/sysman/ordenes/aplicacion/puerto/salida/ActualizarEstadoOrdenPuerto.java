package com.sysman.ordenes.aplicacion.puerto.salida;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;

/** Invoca el procedimiento PL/SQL que valida la transición, aplica optimistic locking y audita. */
public interface ActualizarEstadoOrdenPuerto {

    void actualizarEstado(Long idOrden, EstadoOrden estadoNuevo, Long versionEsperada,
                           String usuarioModifica, String observacion);
}
