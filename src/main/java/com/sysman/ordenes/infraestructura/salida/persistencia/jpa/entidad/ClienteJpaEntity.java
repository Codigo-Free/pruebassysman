package com.sysman.ordenes.infraestructura.salida.persistencia.jpa.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "CLIENTE")
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombre;

    @Column(name = "DOCUMENTO", nullable = false, length = 50)
    private String documento;

    @Column(name = "EMAIL", length = 150)
    private String email;

    @Column(name = "TELEFONO", length = 30)
    private String telefono;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @Column(name = "FECHA_MODIFICACION")
    private Instant fechaModificacion;

    protected ClienteJpaEntity() {
        // requerido por JPA
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
