package com.sysman.ordenes.aplicacion.servicio;

import com.sysman.ordenes.aplicacion.dto.comando.CrearOrdenComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.puerto.salida.ClienteRepositorioPuerto;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.excepcion.ClienteNoEncontradoException;
import com.sysman.ordenes.dominio.modelo.Cliente;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.Orden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearOrdenServiceTest {

    @Mock
    private OrdenRepositorioPuerto ordenRepositorio;

    @Mock
    private ClienteRepositorioPuerto clienteRepositorio;

    private CrearOrdenService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new CrearOrdenService(ordenRepositorio, clienteRepositorio);
    }

    @Test
    void creaUnaOrdenCuandoElClienteExiste() {
        when(clienteRepositorio.buscarPorId(1L)).thenReturn(Optional.of(
                Cliente.reconstruir(1L, "Cliente Uno", "900111222", null, null, null, null)));
        when(ordenRepositorio.guardar(any(Orden.class))).thenAnswer(inv -> {
            Orden orden = inv.getArgument(0);
            return Orden.reconstruir(10L, orden.getIdCliente(), orden.getTipo(), orden.getEstado(),
                    orden.getDescripcion(), orden.getDireccionServicio(), 0L, null, null,
                    orden.getUsuarioCrea(), orden.getUsuarioModifica());
        });

        CrearOrdenComando comando = new CrearOrdenComando(1L, TipoOrden.INSTALACION, "desc", "dir", "sysman");

        OrdenResultado resultado = service.crear(comando);

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.estado()).isEqualTo(EstadoOrden.CREADA);

        ArgumentCaptor<Orden> captor = ArgumentCaptor.forClass(Orden.class);
        verify(ordenRepositorio).guardar(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoOrden.CREADA);
    }

    @Test
    void rechazaLaCreacionSiElClienteNoExiste() {
        when(clienteRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        CrearOrdenComando comando = new CrearOrdenComando(99L, TipoOrden.INSTALACION, "desc", "dir", "sysman");

        assertThatThrownBy(() -> service.crear(comando))
                .isInstanceOf(ClienteNoEncontradoException.class);

        verify(ordenRepositorio, never()).guardar(any());
    }
}
