package com.sysman.ordenes.aplicacion.puerto.entrada;

import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;

public interface ConsultarOrdenUseCase {

    OrdenResultado consultarPorId(Long id);
}
