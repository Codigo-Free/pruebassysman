package com.sysman.ordenes.dominio.modelo;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity inmutable de solo lectura desde el dominio Java: la inserción real de cada
 * registro la realiza el procedimiento PL/SQL {@code SP_ACTUALIZAR_ESTADO_ORDEN}
 * como parte de la misma transacción que actualiza {@code ORDEN}, garantizando
 * que la traza de auditoría nunca quede desincronizada del estado real.
 */
public final class OrdenHistorico {

    private final Long id;
    private final Long idOrden;
    private final EstadoOrden estadoAnterior;
    private final EstadoOrden estadoNuevo;
    private final String usuarioModifica;
    private final Instant fechaModificacion;
    private final String observacion;

    private OrdenHistorico(Long id, Long idOrden, EstadoOrden estadoAnterior, EstadoOrden estadoNuevo,
                            String usuarioModifica, Instant fechaModificacion, String observacion) {
        this.id = id;
        this.idOrden = Objects.requireNonNull(idOrden, "idOrden es obligatorio");
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = Objects.requireNonNull(estadoNuevo, "estadoNuevo es obligatorio");
        this.usuarioModifica = Objects.requireNonNull(usuarioModifica, "usuarioModifica es obligatorio");
        this.fechaModificacion = fechaModificacion;
        this.observacion = observacion;
    }

    public static OrdenHistorico reconstruir(Long id, Long idOrden, EstadoOrden estadoAnterior, EstadoOrden estadoNuevo,
                                              String usuarioModifica, Instant fechaModificacion, String observacion) {
        return new OrdenHistorico(id, idOrden, estadoAnterior, estadoNuevo, usuarioModifica, fechaModificacion, observacion);
    }

    public Long getId() {
        return id;
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public EstadoOrden getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoOrden getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getUsuarioModifica() {
        return usuarioModifica;
    }

    public Instant getFechaModificacion() {
        return fechaModificacion;
    }

    public String getObservacion() {
        return observacion;
    }
}
