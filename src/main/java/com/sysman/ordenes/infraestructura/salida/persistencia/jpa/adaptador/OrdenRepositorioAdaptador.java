package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.adaptador;

import com.sysman.ordenes.aplicacion.dto.resultado.PaginaResultado;
import com.sysman.ordenes.aplicacion.puerto.salida.OrdenRepositorioPuerto;
import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.Orden;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.OrdenJpaEntity;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.mapper.OrdenPersistenciaMapper;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.repositorio.OrdenJpaRepository;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.specification.OrdenSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class OrdenRepositorioAdaptador implements OrdenRepositorioPuerto {

    private final OrdenJpaRepository repositorio;
    private final OrdenPersistenciaMapper mapper;

    public OrdenRepositorioAdaptador(OrdenJpaRepository repositorio, OrdenPersistenciaMapper mapper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    @Override
    public Orden guardar(Orden orden) {
        OrdenJpaEntity entidad = mapper.aEntidadNueva(orden);
        return mapper.aDominio(repositorio.save(entidad));
    }

    @Override
    public Optional<Orden> buscarPorId(Long id) {
        return repositorio.findById(id).map(mapper::aDominio);
    }

    @Override
    public PaginaResultado<Orden> buscar(EstadoOrden estado, Instant fechaInicio, Instant fechaFin,
                                          int pagina, int tamanoPagina) {
        Specification<OrdenJpaEntity> especificacion = Specification
                .where(OrdenSpecifications.conEstado(estado))
                .and(OrdenSpecifications.conFechaDesde(fechaInicio))
                .and(OrdenSpecifications.conFechaHasta(fechaFin));

        Page<OrdenJpaEntity> paginaJpa = repositorio.findAll(especificacion,
                PageRequest.of(pagina, tamanoPagina, Sort.by(Sort.Direction.DESC, "fechaCreacion")));

        return new PaginaResultado<>(
                paginaJpa.getContent().stream().map(mapper::aDominio).toList(),
                pagina,
                tamanoPagina,
                paginaJpa.getTotalElements());
    }
}
