package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.repositorio;

import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.OrdenHistoricoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenHistoricoJpaRepository extends JpaRepository<OrdenHistoricoJpaEntity, Long> {

    List<OrdenHistoricoJpaEntity> findByIdOrdenOrderByFechaModificacionDesc(Long idOrden);
}
