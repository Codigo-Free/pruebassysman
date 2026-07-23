package com.sysman.ordenes.dominio.modelo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClienteTest {

    @Test
    void reconstruyeUnClienteValido() {
        Cliente cliente = Cliente.reconstruir(1L, "Acueducto S.A.", "900123456", "contacto@acueducto.com",
                "3001234567", null, null);

        assertThat(cliente.getNombre()).isEqualTo("Acueducto S.A.");
        assertThat(cliente.getDocumento()).isEqualTo("900123456");
    }

    @Test
    void rechazaNombreVacio() {
        assertThatThrownBy(() -> Cliente.reconstruir(1L, "  ", "900123456", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaDocumentoVacio() {
        assertThatThrownBy(() -> Cliente.reconstruir(1L, "Acueducto S.A.", " ", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
