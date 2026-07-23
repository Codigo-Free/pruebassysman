package com.sysman.ordenes.dominio.modelo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static com.sysman.ordenes.dominio.modelo.EstadoOrden.*;
import static org.assertj.core.api.Assertions.assertThat;

class EstadoOrdenTest {

    @ParameterizedTest(name = "{0} -> {1} es válida")
    @CsvSource({
            "CREADA, ASIGNADA",
            "CREADA, CANCELADA",
            "ASIGNADA, EN_PROCESO",
            "ASIGNADA, CANCELADA",
            "EN_PROCESO, COMPLETADA",
            "EN_PROCESO, CANCELADA"
    })
    void transicionesValidas(EstadoOrden origen, EstadoOrden destino) {
        assertThat(origen.puedeTransicionarA(destino)).isTrue();
    }

    @ParameterizedTest(name = "{0} -> {1} es inválida")
    @CsvSource({
            "CREADA, EN_PROCESO",
            "CREADA, COMPLETADA",
            "ASIGNADA, COMPLETADA",
            "ASIGNADA, CREADA",
            "EN_PROCESO, ASIGNADA",
            "EN_PROCESO, CREADA",
            "COMPLETADA, CREADA",
            "COMPLETADA, CANCELADA",
            "CANCELADA, CREADA",
            "CANCELADA, ASIGNADA"
    })
    void transicionesInvalidas(EstadoOrden origen, EstadoOrden destino) {
        assertThat(origen.puedeTransicionarA(destino)).isFalse();
    }

    @Test
    void estadosTerminales() {
        assertThat(COMPLETADA.esTerminal()).isTrue();
        assertThat(CANCELADA.esTerminal()).isTrue();
    }

    @Test
    void estadosNoTerminales() {
        assertThat(CREADA.esTerminal()).isFalse();
        assertThat(ASIGNADA.esTerminal()).isFalse();
        assertThat(EN_PROCESO.esTerminal()).isFalse();
    }
}
