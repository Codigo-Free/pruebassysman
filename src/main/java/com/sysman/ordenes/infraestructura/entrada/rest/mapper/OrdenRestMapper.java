package com.sysman.ordenes.infraestructura.entrada.rest.mapper;

import com.sysman.ordenes.aplicacion.dto.comando.ActualizarEstadoComando;
import com.sysman.ordenes.aplicacion.dto.comando.CrearOrdenComando;
import com.sysman.ordenes.aplicacion.dto.resultado.OrdenResultado;
import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.ActualizarEstadoRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.request.CrearOrdenRequest;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.response.OrdenResponse;
import com.sysman.ordenes.infraestructura.entrada.rest.dto.response.PaginaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapeo DTO-a-DTO campo a campo: caso de uso ideal para MapStruct (a diferencia del mapper de persistencia). */
@Mapper(componentModel = "spring")
public interface OrdenRestMapper {

    CrearOrdenComando aComando(CrearOrdenRequest request);

    @Mapping(target = "idOrden", source = "idOrden")
    @Mapping(target = "estadoNuevo", source = "request.estadoNuevo")
    @Mapping(target = "versionEsperada", source = "request.versionEsperada")
    @Mapping(target = "usuarioModifica", source = "request.usuarioModifica")
    @Mapping(target = "observacion", source = "request.observacion")
    ActualizarEstadoComando aComando(Long idOrden, ActualizarEstadoRequest request);

    OrdenResponse aResponse(OrdenResultado resultado);

    default PaginaResponse<OrdenResponse> aResponsePagina(PaginaResultado<OrdenResultado> pagina) {
        return new PaginaResponse<>(
                pagina.contenido().stream().map(this::aResponse).toList(),
                pagina.pagina(),
                pagina.tamanoPagina(),
                pagina.totalElementos(),
                pagina.totalPaginas());
    }
}
