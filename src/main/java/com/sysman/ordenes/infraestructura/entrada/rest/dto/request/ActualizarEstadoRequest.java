package com.sysman.ordenes.infraestructura.entrada.rest.dto.request;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarEstadoRequest(
        @NotNull EstadoOrden estadoNuevo,
        @NotNull Long versionEsperada,
        @NotBlank String usuarioModifica,
        @Size(max = 500) String observacion
) {
}
