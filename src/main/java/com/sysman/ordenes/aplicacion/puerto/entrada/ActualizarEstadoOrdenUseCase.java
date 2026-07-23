package com.sysman.ordenes.aplicacion.puerto.entrada;

import com.sysman.ordenes.aplicacion.dto.comando.ActualizarEstadoComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;

public interface ActualizarEstadoOrdenUseCase {

    OrdenResultado actualizarEstado(ActualizarEstadoComando comando);
}
