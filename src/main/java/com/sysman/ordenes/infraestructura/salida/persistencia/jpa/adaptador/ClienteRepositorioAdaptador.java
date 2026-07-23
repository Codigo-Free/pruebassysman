package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.adaptador;

import com.sysman.ordenes.aplicacion.puerto.salida.ClienteRepositorioPuerto;
import com.sysman.ordenes.dominio.modelo.Cliente;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.mapper.ClientePersistenciaMapper;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.repositorio.ClienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClienteRepositorioAdaptador implements ClienteRepositorioPuerto {

    private final ClienteJpaRepository repositorio;
    private final ClientePersistenciaMapper mapper;

    public ClienteRepositorioAdaptador(ClienteJpaRepository repositorio, ClientePersistenciaMapper mapper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return repositorio.findById(id).map(mapper::aDominio);
    }
}
