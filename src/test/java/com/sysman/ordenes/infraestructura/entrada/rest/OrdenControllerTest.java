package com.sysman.ordenes.infraestructura.entrada.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;
import com.sysman.ordenes.aplicacion.puerto.entrada.ActualizarEstadoOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.entrada.ConsultarOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.entrada.CrearOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.entrada.ListarOrdenesUseCase;
import com.sysman.ordenes.dominio.excepcion.ClienteNoEncontradoException;
import com.sysman.ordenes.dominio.excepcion.ConflictoVersionOrdenException;
import com.sysman.ordenes.dominio.excepcion.OrdenNoEncontradaException;
import com.sysman.ordenes.dominio.excepcion.TransicionEstadoInvalidaException;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.ActualizarEstadoRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.CrearOrdenRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.mapper.OrdenRestMapperImpl;
import com.sysman.ordenes.infraestructura.transversal.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdenController.class)
@Import({GlobalExceptionHandler.class, OrdenRestMapperImpl.class})
class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CrearOrdenUseCase crearOrdenUseCase;

    @MockitoBean
    private ConsultarOrdenUseCase consultarOrdenUseCase;

    @MockitoBean
    private ActualizarEstadoOrdenUseCase actualizarEstadoOrdenUseCase;

    @MockitoBean
    private ListarOrdenesUseCase listarOrdenesUseCase;

    private static OrdenResultado ordenResultadoDeEjemplo() {
        return new OrdenResultado(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.CREADA, "desc", "dir",
                0L, null, null, "sysman", "sysman");
    }

    @Test
    void creaUnaOrdenYDevuelve201() throws Exception {
        when(crearOrdenUseCase.crear(any())).thenReturn(ordenResultadoDeEjemplo());

        CrearOrdenRequest request = new CrearOrdenRequest(1L, TipoOrden.INSTALACION, "desc", "dir", "sysman");

        mockMvc.perform(post("/api/v1/orden")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CREADA"));
    }

    @Test
    void rechazaCrearOrdenSinIdClienteConError400() throws Exception {
        CrearOrdenRequest request = new CrearOrdenRequest(null, TipoOrden.INSTALACION, "desc", "dir", "sysman");

        mockMvc.perform(post("/api/v1/orden")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultaUnaOrdenExistente() throws Exception {
        when(consultarOrdenUseCase.consultarPorId(1L)).thenReturn(ordenResultadoDeEjemplo());

        mockMvc.perform(get("/api/v1/orden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void devuelve404SiLaOrdenNoExiste() throws Exception {
        when(consultarOrdenUseCase.consultarPorId(99L)).thenThrow(new OrdenNoEncontradaException(99L));

        mockMvc.perform(get("/api/v1/orden/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizaElEstadoDeUnaOrden() throws Exception {
        OrdenResultado actualizado = new OrdenResultado(1L, 1L, TipoOrden.INSTALACION, EstadoOrden.ASIGNADA, "desc",
                "dir", 1L, null, null, "sysman", "operador1");
        when(actualizarEstadoOrdenUseCase.actualizarEstado(any())).thenReturn(actualizado);

        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoOrden.ASIGNADA, 0L, "operador1", null);

        mockMvc.perform(put("/api/v1/orden/1/estado")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ASIGNADA"))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void devuelve422SiLaTransicionEsInvalida() throws Exception {
        when(actualizarEstadoOrdenUseCase.actualizarEstado(any()))
                .thenThrow(new TransicionEstadoInvalidaException(EstadoOrden.CREADA, EstadoOrden.COMPLETADA));

        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoOrden.COMPLETADA, 0L, "operador1", null);

        mockMvc.perform(put("/api/v1/orden/1/estado")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422));
    }

    @Test
    void devuelve409SiHayConflictoDeVersion() throws Exception {
        when(actualizarEstadoOrdenUseCase.actualizarEstado(any()))
                .thenThrow(new ConflictoVersionOrdenException(1L));

        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoOrden.ASIGNADA, 0L, "operador1", null);

        mockMvc.perform(put("/api/v1/orden/1/estado")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void listaOrdenesConFiltros() throws Exception {
        when(listarOrdenesUseCase.listar(any()))
                .thenReturn(new PaginaResultado<>(List.of(ordenResultadoDeEjemplo()), 0, 20, 1L));

        mockMvc.perform(get("/api/v1/orden").param("estado", "CREADA").param("pagina", "0").param("tamanoPagina", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido.length()").value(1))
                .andExpect(jsonPath("$.totalElementos").value(1));
    }

    @Test
    void devuelve404SiElClienteNoExisteAlCrear() throws Exception {
        when(crearOrdenUseCase.crear(any())).thenThrow(new ClienteNoEncontradoException(1L));

        CrearOrdenRequest request = new CrearOrdenRequest(1L, TipoOrden.INSTALACION, "desc", "dir", "sysman");

        mockMvc.perform(post("/api/v1/orden")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
