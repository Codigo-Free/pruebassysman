package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad;

import com.sysman.ordenes.dominio.modelo.EstadoOrden;
import com.sysman.ordenes.dominio.modelo.TipoOrden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Table(name = "ORDEN")
public class OrdenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORDEN")
    private Long id;

    @Column(name = "ID_CLIENTE", nullable = false)
    private Long idCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ORDEN", nullable = false, length = 30)
    private TipoOrden tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 30)
    private EstadoOrden estado;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "DIRECCION_SERVICIO", length = 300)
    private String direccionServicio;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @Column(name = "FECHA_MODIFICACION")
    private Instant fechaModificacion;

    @Column(name = "USUARIO_CREA", updatable = false, length = 100)
    private String usuarioCrea;

    @Column(name = "USUARIO_MODIFICA", length = 100)
    private String usuarioModifica;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    protected OrdenJpaEntity() {
        // requerido por JPA
    }

    public OrdenJpaEntity(Long idCliente, TipoOrden tipo, EstadoOrden estado, String descripcion,
                           String direccionServicio, String usuarioCrea, String usuarioModifica) {
        this.idCliente = idCliente;
        this.tipo = tipo;
        this.estado = estado;
        this.descripcion = descripcion;
        this.direccionServicio = direccionServicio;
        this.usuarioCrea = usuarioCrea;
        this.usuarioModifica = usuarioModifica;
    }

    @PrePersist
    void alPersistir() {
        this.fechaCreacion = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public TipoOrden getTipo() {
        return tipo;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDireccionServicio() {
        return direccionServicio;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaModificacion() {
        return fechaModificacion;
    }

    public String getUsuarioCrea() {
        return usuarioCrea;
    }

    public String getUsuarioModifica() {
        return usuarioModifica;
    }

    public Long getVersion() {
        return version;
    }
}
