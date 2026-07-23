package com.sysman.ordenes.aplicacion.puerto.salida;

import com.sysman.ordenes.dominio.modelo.Cliente;

import java.util.Optional;

public interface ClienteRepositorioPuerto {

    Optional<Cliente> buscarPorId(Long id);
}
