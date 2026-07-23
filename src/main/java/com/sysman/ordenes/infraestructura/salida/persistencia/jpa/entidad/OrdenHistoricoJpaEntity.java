package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** Solo lectura desde la aplicación: las filas las inserta {@code SP_ACTUALIZAR_ESTADO_ORDEN}. */
@Entity
@Table(name = "ORDEN_HISTORICO")
public class OrdenHistoricoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HISTORICO")
    private Long id;

    @Column(name = "ID_ORDEN", nullable = false)
    private Long idOrden;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ANTERIOR", length = 30)
    private EstadoOrden estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_NUEVO", nullable = false, length = 30)
    private EstadoOrden estadoNuevo;

    @Column(name = "USUARIO_MODIFICA", nullable = false, length = 100)
    private String usuarioModifica;

    @Column(name = "FECHA_MODIFICACION", nullable = false)
    private Instant fechaModificacion;

    @Column(name = "OBSERVACION", length = 500)
    private String observacion;

    protected OrdenHistoricoJpaEntity() {
        // requerido por JPA
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
