package com.sysman.ordenes.infraestructura.entrada.rest;

import com.sysman.ordenes.aplicacion.dto.comando.ListarOrdenesComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.puerto.entrada.ActualizarEstadoOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.entrada.ConsultarOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.entrada.CrearOrdenUseCase;
import com.sysman.ordenes.aplicacion.puerto.entrada.ListarOrdenesUseCase;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.ActualizarEstadoRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.CrearOrdenRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.response.OrdenResponse;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.response.PaginaResponse;
import com.sysman.ordenes.infraestructura.entrada.rest.mapper.OrdenRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/orden")
@Tag(name = "Órdenes", description = "Gestión de órdenes operativas")
public class OrdenController {

    private final CrearOrdenUseCase crearOrdenUseCase;
    private final ConsultarOrdenUseCase consultarOrdenUseCase;
    private final ActualizarEstadoOrdenUseCase actualizarEstadoOrdenUseCase;
    private final ListarOrdenesUseCase listarOrdenesUseCase;
    private final OrdenRestMapper mapper;

    public OrdenController(CrearOrdenUseCase crearOrdenUseCase, ConsultarOrdenUseCase consultarOrdenUseCase,
                            ActualizarEstadoOrdenUseCase actualizarEstadoOrdenUseCase,
                            ListarOrdenesUseCase listarOrdenesUseCase, OrdenRestMapper mapper) {
        this.crearOrdenUseCase = crearOrdenUseCase;
        this.consultarOrdenUseCase = consultarOrdenUseCase;
        this.actualizarEstadoOrdenUseCase = actualizarEstadoOrdenUseCase;
        this.listarOrdenesUseCase = listarOrdenesUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una orden operativa")
    public OrdenResponse crear(@Valid @RequestBody CrearOrdenRequest request) {
        OrdenResultado resultado = crearOrdenUseCase.crear(mapper.aComando(request));
        return mapper.aResponse(resultado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una orden por id")
    public OrdenResponse consultar(@PathVariable Long id) {
        return mapper.aResponse(consultarOrdenUseCase.consultarPorId(id));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de una orden (optimistic locking)")
    public OrdenResponse actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoRequest request) {
        OrdenResultado resultado = actualizarEstadoOrdenUseCase.actualizarEstado(mapper.aComando(id, request));
        return mapper.aResponse(resultado);
    }

    @GetMapping
    @Operation(summary = "Listar órdenes con paginación y filtros de estado y rango de fechas")
    public PaginaResponse<OrdenResponse> listar(
            @RequestParam(required = false) EstadoOrden estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaFin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanoPagina) {

        ListarOrdenesComando comando = new ListarOrdenesComando(estado, fechaInicio, fechaFin, pagina, tamanoPagina);
        return mapper.aResponsePagina(listarOrdenesUseCase.listar(comando));
    }
}
