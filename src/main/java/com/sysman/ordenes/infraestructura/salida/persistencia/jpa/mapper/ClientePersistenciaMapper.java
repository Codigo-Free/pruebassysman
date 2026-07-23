package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.mapper;

import com.sysman.ordenes.dominio.modelo.Cliente;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.ClienteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientePersistenciaMapper {

    public Cliente aDominio(ClienteJpaEntity entidad) {
        return Cliente.reconstruir(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getDocumento(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getFechaCreacion(),
                entidad.getFechaModificacion());
    }
}
