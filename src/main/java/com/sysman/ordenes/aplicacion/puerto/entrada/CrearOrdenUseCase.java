package com.sysman.ordenes.aplicacion.puerto.entrada;

import com.sysman.ordenes.aplicacion.dto.comando.CrearOrdenComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;

public interface CrearOrdenUseCase {

    OrdenResultado crear(CrearOrdenComando comando);
}
