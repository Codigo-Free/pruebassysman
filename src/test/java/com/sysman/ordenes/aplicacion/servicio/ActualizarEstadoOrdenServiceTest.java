package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.comando.ActualizarEstadoComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.puerto.salida.ActualizarEstadoOrdenPuerto;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.excepcion.OrdenNoEncontradaException;
import com.sysman.ordenes.dominio.excepcion.TransicionEstadoInvalidaException;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.Orden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoOrdenServiceTest {

    @Mock
    private OrdenRepositorioPuerto ordenRepositorio;

    @Mock
    private ActualizarEstadoOrdenPuerto actualizarEstadoPuerto;

    private ActualizarEstadoOrdenService service;

    @BeforeEach
    void setUp() {
        service = new ActualizarEstadoOrdenService(ordenRepositorio, actualizarEstadoPuerto);
    }

    @Test
    void actualizaElEstadoCuandoLaTransicionEsValida() {
        Orden ordenCreada = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.CREADA, "d", "dir",
                0L, null, null, "sysman", "sysman");
        Orden ordenAsignada = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.ASIGNADA, "d", "dir",
                1L, null, null, "sysman", "operador1");

        when(ordenRepositorio.buscarPorId(1L)).thenReturn(Optional.of(ordenCreada), Optional.of(ordenAsignada));

        ActualizarEstadoComando comando = new ActualizarEstadoComando(1L, EstadoOrden.ASIGNADA, 0L, "operador1", null);

        OrdenResultado resultado = service.actualizarEstado(comando);

        assertThat(resultado.estado()).isEqualTo(EstadoOrden.ASIGNADA);
        verify(actualizarEstadoPuerto).actualizarEstado(eq(1L), eq(EstadoOrden.ASIGNADA), eq(0L),
                eq("operador1"), any());
    }

    @Test
    void rechazaEnMemoriaUnaTransicionInvalidaSinLlamarAlPuerto() {
        Orden ordenCreada = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.CREADA, "d", "dir",
                0L, null, null, "sysman", "sysman");
        when(ordenRepositorio.buscarPorId(1L)).thenReturn(Optional.of(ordenCreada));

        ActualizarEstadoComando comando = new ActualizarEstadoComando(1L, EstadoOrden.COMPLETADA, 0L, "operador1", null);

        assertThatThrownBy(() -> service.actualizarEstado(comando))
                .isInstanceOf(TransicionEstadoInvalidaException.class);

        verify(actualizarEstadoPuerto, never()).actualizarEstado(any(), any(), any(), anyString(), any());
    }

    @Test
    void lanzaExcepcionSiLaOrdenNoExiste() {
        when(ordenRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        ActualizarEstadoComando comando = new ActualizarEstadoComando(99L, EstadoOrden.ASIGNADA, 0L, "operador1", null);

        assertThatThrownBy(() -> service.actualizarEstado(comando))
                .isInstanceOf(OrdenNoEncontradaException.class);
    }
}
