package com.sysman.ordenes.aplicacion.dto.resultado;

import java.util.List;
import java.util.function.Function;

/**
 * Envoltorio de paginación propio de la aplicación (no acoplado a {@code org.springframework.data.domain.Page}),
 * para que los puertos y casos de uso permanezcan libres de tipos de framework.
 */
public record PaginaResultado<T>(
        List<T> contenido,
        int pagina,
        int tamanoPagina,
        long totalElementos
) {

    public <R> PaginaResultado<R> mapear(Function<T, R> mapeador) {
        return new PaginaResultado<>(contenido.stream().map(mapeador).toList(), pagina, tamanoPagina, totalElementos);
    }

    public int totalPaginas() {
        return tamanoPagina == 0 ? 0 : (int) Math.ceil((double) totalElementos / tamanoPagina);
    }
}
