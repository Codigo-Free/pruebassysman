package com.sysman.ordenes.infraestructura.entrada.rest.dto.response;

import java.util.List;

public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanoPagina,
        long totalElementos,
        int totalPaginas
) {
}
