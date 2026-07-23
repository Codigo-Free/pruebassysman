package com.sysman.ordenes.aplicacion.puerto.entrada;

import com.sysman.ordenes.aplicacion.dto.comando.ListarOrdenesComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;

public interface ListarOrdenesUseCase {

    PaginaResultado<OrdenResultado> listar(ListarOrdenesComando comando);
}
