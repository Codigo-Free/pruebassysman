package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.puerto.entrada.ConsultarOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.excepcion.OrdenNoEncontradaException;
import com.sysman.ordenes.dominio.modelo.Orden;
import org.springframework.stereotype.Service;

@Service
public class ConsultarOrdenService implements ConsultarOrdenUseCase {

    private final OrdenRepositorioPuerto ordenRepositorio;

    public ConsultarOrdenService(OrdenRepositorioPuerto ordenRepositorio) {
        this.ordenRepositorio = ordenRepositorio;
    }

    @Override
    public OrdenResultado consultarPorId(Long id) {
        Orden orden = ordenRepositorio.buscarPorId(id)
                .orElseThrow(() -> new OrdenNoEncontradaException(id));

        return OrdenResultado.desde(orden);
    }
}
