package com.sysman.ordenes.infraestructura.entrada.rest.dto.request;

import com.sysman.ordenes.dominio.modelo.TipoOrden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearOrdenRequest(
        @NotNull Long idCliente,
        @NotNull TipoOrden tipo,
        @Size(max = 1000) String descripcion,
        @Size(max = 300) String direccionServicio,
        @NotBlank String usuarioCrea
) {
}
