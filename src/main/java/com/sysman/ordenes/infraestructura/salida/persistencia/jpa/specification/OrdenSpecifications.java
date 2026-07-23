package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.specification;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.OrdenJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

/** Patrón Specification: filtros combinables para GET /orden?estado=&fechaInicio=&fechaFin=. */
public final class OrdenSpecifications {

    private OrdenSpecifications() {
    }

    public static Specification<OrdenJpaEntity> conEstado(EstadoOrden estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<OrdenJpaEntity> conFechaDesde(Instant fechaInicio) {
        return (root, query, cb) ->
                fechaInicio == null ? null : cb.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaInicio);
    }

    public static Specification<OrdenJpaEntity> conFechaHasta(Instant fechaFin) {
        return (root, query, cb) ->
                fechaFin == null ? null : cb.lessThanOrEqualTo(root.get("fechaCreacion"), fechaFin);
    }
}
