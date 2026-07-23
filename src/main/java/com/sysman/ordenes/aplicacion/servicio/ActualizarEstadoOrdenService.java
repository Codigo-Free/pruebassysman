package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.comando.ActualizarEstadoComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.puerto.entrada.ActualizarEstadoOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.salida.ActualizarEstadoOrdenPuerto;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.excepcion.OrdenNoEncontradaException;
import com.sysman.ordenes.dominio.modelo.Orden;
import org.springframework.stereotype.Service;

/**
 * Deliberadamente sin {@code @Transactional}: la atomicidad de la actualización de
 * estado y la inserción en el histórico la garantiza {@code SP_ACTUALIZAR_ESTADO_ORDEN}
 * como una única transacción PL/SQL (ver docs/decisiones-tecnicas.md, ADR sobre
 * transacciones). La validación en memoria es solo fail-fast; el procedimiento
 * revalida la transición y la versión de forma atómica y es la autoridad final.
 */
@Service
public class ActualizarEstadoOrdenService implements ActualizarEstadoOrdenUseCase {

    private final OrdenRepositorioPuerto ordenRepositorio;
    private final ActualizarEstadoOrdenPuerto actualizarEstadoPuerto;

    public ActualizarEstadoOrdenService(OrdenRepositorioPuerto ordenRepositorio,
                                         ActualizarEstadoOrdenPuerto actualizarEstadoPuerto) {
        this.ordenRepositorio = ordenRepositorio;
        this.actualizarEstadoPuerto = actualizarEstadoPuerto;
    }

    @Override
    public OrdenResultado actualizarEstado(ActualizarEstadoComando comando) {
        Orden ordenActual = ordenRepositorio.buscarPorId(comando.idOrden())
                .orElseThrow(() -> new OrdenNoEncontradaException(comando.idOrden()));

        ordenActual.validarTransicionA(comando.estadoNuevo());

        actualizarEstadoPuerto.actualizarEstado(comando.idOrden(), comando.estadoNuevo(),
                comando.versionEsperada(), comando.usuarioModifica(), comando.observacion());

        Orden ordenActualizada = ordenRepositorio.buscarPorId(comando.idOrden())
                .orElseThrow(() -> new OrdenNoEncontradaException(comando.idOrden()));

        return OrdenResultado.desde(ordenActualizada);
    }
}
