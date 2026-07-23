package com.sysman.ordenes.dominio.modelo;

import com.sysman.ordenes.dominio.servicio.ValidadorTransicionEstado;

import java.time.Instant;
import java.util.Objects;

/** Aggregate Root. Invariante principal: solo puede cambiar de estado siguiendo {@link EstadoOrden}. */
public final class Orden {

    private static final ValidadorTransicionEstado VALIDADOR = new ValidadorTransicionEstado();

    private final Long id;
    private final Long idCliente;
    private final TipoOrden tipo;
    private final EstadoOrden estado;
    private final String descripcion;
    private final String direccionServicio;
    private final Long version;
    private final Instant fechaCreacion;
    private final Instant fechaModificacion;
    private final String usuarioCrea;
    private final String usuarioModifica;

    private Orden(Long id, Long idCliente, TipoOrden tipo, EstadoOrden estado, String descripcion,
                  String direccionServicio, Long version, Instant fechaCreacion, Instant fechaModificacion,
                  String usuarioCrea, String usuarioModifica) {
        this.id = id;
        this.idCliente = Objects.requireNonNull(idCliente, "idCliente es obligatorio");
        this.tipo = Objects.requireNonNull(tipo, "tipo es obligatorio");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
        this.descripcion = descripcion;
        this.direccionServicio = direccionServicio;
        this.version = version;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.usuarioCrea = usuarioCrea;
        this.usuarioModifica = usuarioModifica;
    }

    /** Crea una orden nueva, siempre en estado {@link EstadoOrden#CREADA}. */
    public static Orden crear(Long idCliente, TipoOrden tipo, String descripcion, String direccionServicio,
                               String usuarioCrea) {
        return new Orden(null, idCliente, tipo, EstadoOrden.CREADA, descripcion, direccionServicio,
                null, null, null, usuarioCrea, usuarioCrea);
    }

    /** Reconstruye una orden ya persistida (usado por los adaptadores de salida). */
    public static Orden reconstruir(Long id, Long idCliente, TipoOrden tipo, EstadoOrden estado, String descripcion,
                                     String direccionServicio, Long version, Instant fechaCreacion,
                                     Instant fechaModificacion, String usuarioCrea, String usuarioModifica) {
        return new Orden(id, idCliente, tipo, estado, descripcion, direccionServicio, version,
                fechaCreacion, fechaModificacion, usuarioCrea, usuarioModifica);
    }

    /**
     * Valida en memoria (fail-fast) si la transición al estado destino es válida.
     * No es la autoridad final: el procedimiento PL/SQL revalida contra
     * {@code ORDEN_TRANSICION_VALIDA} de forma atómica junto con la actualización.
     */
    public void validarTransicionA(EstadoOrden estadoDestino) {
        VALIDADOR.validar(this.estado, estadoDestino);
    }

    public boolean esTerminal() {
        return estado.esTerminal();
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

    public Long getVersion() {
        return version;
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
}
