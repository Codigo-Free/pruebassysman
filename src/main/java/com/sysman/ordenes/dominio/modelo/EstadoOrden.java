package com.sysman.ordenes.dominio.modelo;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Value Object con la máquina de estados de una {@link Orden}.
 *
 * <p>Esta matriz es la fuente de verdad para la validación "fail-fast" en Java,
 * pero la autoridad final de la transición es el procedimiento PL/SQL
 * {@code SP_ACTUALIZAR_ESTADO_ORDEN}, que lee la misma matriz desde la tabla
 * {@code ORDEN_TRANSICION_VALIDA}. Ambas deben mantenerse sincronizadas
 * (ver {@code docs/decisiones-tecnicas.md}).
 */
public enum EstadoOrden {
    CREADA,
    ASIGNADA,
    EN_PROCESO,
    COMPLETADA,
    CANCELADA;

    private static final Map<EstadoOrden, Set<EstadoOrden>> TRANSICIONES_VALIDAS = Map.of(
            CREADA, EnumSet.of(ASIGNADA, CANCELADA),
            ASIGNADA, EnumSet.of(EN_PROCESO, CANCELADA),
            EN_PROCESO, EnumSet.of(COMPLETADA, CANCELADA),
            COMPLETADA, EnumSet.noneOf(EstadoOrden.class),
            CANCELADA, EnumSet.noneOf(EstadoOrden.class)
    );

    public boolean puedeTransicionarA(EstadoOrden destino) {
        return TRANSICIONES_VALIDAS.get(this).contains(destino);
    }

    public Set<EstadoOrden> transicionesValidas() {
        return TRANSICIONES_VALIDAS.get(this);
    }

    public boolean esTerminal() {
        return TRANSICIONES_VALIDAS.get(this).isEmpty();
    }
}
