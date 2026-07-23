package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.excepcion.OrdenNoEncontradaException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarOrdenServiceTest {

    @Mock
    private OrdenRepositorioPuerto ordenRepositorio;

    private ConsultarOrdenService service;

    @BeforeEach
    void setUp() {
        service = new ConsultarOrdenService(ordenRepositorio);
    }

    @Test
    void devuelveLaOrdenCuandoExiste() {
        Orden orden = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.CREADA, "d", "dir",
                0L, null, null, "sysman", "sysman");
        when(ordenRepositorio.buscarPorId(1L)).thenReturn(Optional.of(orden));

        OrdenResultado resultado = service.consultarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.estado()).isEqualTo(EstadoOrden.CREADA);
    }

    @Test
    void lanzaExcepcionSiLaOrdenNoExiste() {
        when(ordenRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarPorId(99L))
                .isInstanceOf(OrdenNoEncontradaException.class);
    }
}
