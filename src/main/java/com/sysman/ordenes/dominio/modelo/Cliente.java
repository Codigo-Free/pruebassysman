package com.sysman.ordenes.dominio.modelo;

import java.time.Instant;
import java.util.Objects;

/** Aggregate Root. Referenciado por {@link Orden} mediante su identificador, no embebido. */
public final class Cliente {

    private final Long id;
    private final String nombre;
    private final String documento;
    private final String email;
    private final String telefono;
    private final Instant fechaCreacion;
    private final Instant fechaModificacion;

    private Cliente(Long id, String nombre, String documento, String email, String telefono,
                     Instant fechaCreacion, Instant fechaModificacion) {
        this.nombre = Objects.requireNonNull(nombre, "nombre es obligatorio");
        this.documento = Objects.requireNonNull(documento, "documento es obligatorio");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("nombre no puede estar vacío");
        }
        if (documento.isBlank()) {
            throw new IllegalArgumentException("documento no puede estar vacío");
        }
        this.id = id;
        this.email = email;
        this.telefono = telefono;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
    }

    public static Cliente reconstruir(Long id, String nombre, String documento, String email, String telefono,
                                       Instant fechaCreacion, Instant fechaModificacion) {
        return new Cliente(id, nombre, documento, email, telefono, fechaCreacion, fechaModificacion);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaModificacion() {
        return fechaModificacion;
    }
}
