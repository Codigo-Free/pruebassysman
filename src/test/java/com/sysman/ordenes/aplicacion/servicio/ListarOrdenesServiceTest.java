package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.comando.ListarOrdenesComando;
import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.Orden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarOrdenesServiceTest {

    @Mock
    private OrdenRepositorioPuerto ordenRepositorio;

    private ListarOrdenesService service;

    @BeforeEach
    void setUp() {
        service = new ListarOrdenesService(ordenRepositorio);
    }

    @Test
    void mapeaLaPaginaDeDominioAResultado() {
        Orden orden = Orden.reconstruir(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.ASIGNADA, "d", "dir",
                1L, null, null, "sysman", "sysman");
        Instant fechaInicio = Instant.now().minusSeconds(3600);
        Instant fechaFin = Instant.now();

        when(ordenRepositorio.buscar(EstadoOrden.ASIGNADA, fechaInicio, fechaFin, 0, 20))
                .thenReturn(new PaginaResultado<>(List.of(orden), 0, 20, 1L));

        ListarOrdenesComando comando = new ListarOrdenesComando(EstadoOrden.ASIGNADA, fechaInicio, fechaFin, 0, 20);

        PaginaResultado<?> resultado = service.listar(comando);

        assertThat(resultado.contenido()).hasSize(1);
        assertThat(resultado.totalElementos()).isEqualTo(1L);
    }
}
