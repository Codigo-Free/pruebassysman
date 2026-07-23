package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.comando.ListarOrdenesComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;
import com.sysman.ordenes.aplicacion.puerto.entrada.ListarOrdenesUseCase;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.modelo.Orden;
import org.springframework.stereotype.Service;

@Service
public class ListarOrdenesService implements ListarOrdenesUseCase {

    private final OrdenRepositorioPuerto ordenRepositorio;

    public ListarOrdenesService(OrdenRepositorioPuerto ordenRepositorio) {
        this.ordenRepositorio = ordenRepositorio;
    }

    @Override
    public PaginaResultado<OrdenResultado> listar(ListarOrdenesComando comando) {
        PaginaResultado<Orden> pagina = ordenRepositorio.buscar(
                comando.estado(), comando.fechaInicio(), comando.fechaFin(),
                comando.pagina(), comando.tamanoPagina());

        return pagina.mapear(OrdenResultado::desde);
    }
}
