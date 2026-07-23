package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.repositorio;

import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.OrdenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrdenJpaRepository extends JpaRepository<OrdenJpaEntity, Long>,
        JpaSpecificationExecutor<OrdenJpaEntity> {
}
