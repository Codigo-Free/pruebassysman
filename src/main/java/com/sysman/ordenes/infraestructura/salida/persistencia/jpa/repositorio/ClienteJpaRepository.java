package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.repositorio;

import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Long> {
}
