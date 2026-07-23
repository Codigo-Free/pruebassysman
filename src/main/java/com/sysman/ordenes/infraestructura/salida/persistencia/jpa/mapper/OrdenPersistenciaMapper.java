package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.mapper;

import com.sysman.ordenes.dominio.modelo.Orden;
import com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad.OrdenJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapeo manual (no MapStruct): {@link Orden} es inmutable y se construye mediante
 * métodos de fábrica ({@code crear}/{@code reconstruir}), un patrón que MapStruct no
 * genera automáticamente. MapStruct sí se usa en el mapper REST (DTO-a-DTO simple).
 */
@Component
public class OrdenPersistenciaMapper {

    public OrdenJpaEntity aEntidadNueva(Orden orden) {
        return new OrdenJpaEntity(
                orden.getIdCliente(),
                orden.getTipo(),
                orden.getEstado(),
                orden.getDescripcion(),
                orden.getDireccionServicio(),
                orden.getUsuarioCrea(),
                orden.getUsuarioModifica());
    }

    public Orden aDominio(OrdenJpaEntity entidad) {
        return Orden.reconstruir(
                entidad.getId(),
                entidad.getIdCliente(),
                entidad.getTipo(),
                entidad.getEstado(),
                entidad.getDescripcion(),
                entidad.getDireccionServicio(),
                entidad.getVersion(),
                entidad.getFechaCreacion(),
                entidad.getFechaModificacion(),
                entidad.getUsuarioCrea(),
                entidad.getUsuarioModifica());
    }
}
