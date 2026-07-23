package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.comando.CrearOrdenComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.puerto.entrada.CrearOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.salida.ClienteRepositorioPuerto;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.excepcion.ClienteNoEncontradoException;
import com.sysman.ordenes.dominio.modelo.Orden;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearOrdenService implements CrearOrdenUseCase {

    private final OrdenRepositorioPuerto ordenRepositorio;
    private final ClienteRepositorioPuerto clienteRepositorio;

    public CrearOrdenService(OrdenRepositorioPuerto ordenRepositorio, ClienteRepositorioPuerto clienteRepositorio) {
        this.ordenRepositorio = ordenRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    /**
     * Transacción a nivel de aplicación: si falla la validación del cliente o la
     * inserción, se hace rollback automático (ver docs/propuesta-arquitectura.md).
     */
    @Override
    @Transactional
    public OrdenResultado crear(CrearOrdenComando comando) {
        clienteRepositorio.buscarPorId(comando.idCliente())
                .orElseThrow(() -> new ClienteNoEncontradoException(comando.idCliente()));

        Orden nuevaOrden = Orden.crear(comando.idCliente(), comando.tipo(), comando.descripcion(),
                comando.direccionServicio(), comando.usuarioCrea());

        Orden guardada = ordenRepositorio.guardar(nuevaOrden);

        return OrdenResultado.desde(guardada);
    }
}
