package com.sysman.ordenes.dominio.modelo;

import com.sysman.ordenes.dominio.excepcion.TransicionEstadoInvalidaException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdenTest {

    @Test
    void unaOrdenNuevaSiempreInicioEnCreada() {
        Orden orden = Orden.crear(1L, TipoOrden.INSTALACION, "Instalar medidor", "Calle 1 # 2-3", "sysman");

        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.CREADA);
        assertThat(orden.getId()).isNull();
        assertThat(orden.getIdCliente()).isEqualTo(1L);
    }

    @Test
    void noPermiteCrearOrdenSinCliente() {
        assertThatThrownBy(() -> Orden.crear(null, TipoOrden.INSTALACION, "desc", "dir", "sysman"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void validaTransicionPermitidaSinLanzarExcepcion() {
        Orden orden = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.CREADA, "d", "dir",
                0L, null, null, "sysman", "sysman");

        assertThatCode(() -> orden.validarTransicionA(EstadoOrden.ASIGNADA)).doesNotThrowAnyException();
    }

    @Test
    void rechazaTransicionInvalida() {
        Orden orden = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.CREADA, "d", "dir",
                0L, null, null, "sysman", "sysman");

        assertThatThrownBy(() -> orden.validarTransicionA(EstadoOrden.COMPLETADA))
                .isInstanceOf(TransicionEstadoInvalidaException.class)
                .hasMessageContaining("CREADA")
                .hasMessageContaining("COMPLETADA");
    }

    @Test
    void unaOrdenEnEstadoTerminalNoTienesMasTransiciones() {
        Orden orden = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.COMPLETADA, "d", "dir",
                2L, null, null, "sysman", "sysman");

        assertThat(orden.esTerminal()).isTrue();
    }
}
