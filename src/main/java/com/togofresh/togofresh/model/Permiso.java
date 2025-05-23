package com.togofresh.togofresh.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permisos")
public class Permiso {

    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre del permiso debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z_]+$", message = "Solo letras y guiones bajos permitidos")
    @Column(name = "nombre_permiso", unique = true, nullable = false, length = 50)
    private String nombrePermiso;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha_creacion", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion;

    // -------------------- Constructores --------------------
    public Permiso() {
        // Constructor vacío requerido por JPA
    }

    public Permiso(String nombrePermiso, String descripcion) {
        this.nombrePermiso = nombrePermiso;
        this.descripcion = descripcion;
    }

    // -------------------- Getters y Setters --------------------
    public Long getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Long idPermiso) {
        this.idPermiso = idPermiso;
    }

    public String getNombrePermiso() {
        return nombrePermiso;
    }

    public void setNombrePermiso(String nombrePermiso) {
        this.nombrePermiso = nombrePermiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // -------------------- Métodos de Auditoría --------------------
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // -------------------- Métodos de Negocio --------------------
    @Override
    public String toString() {
        return "Permiso{" +
                "idPermiso=" + idPermiso +
                ", nombrePermiso='" + nombrePermiso + '\'' +
                '}';
    }

    /**
     * Método para verificar si el permiso coincide con el nombre dado
     * @param permisoNombre Nombre del permiso a comparar
     * @return true si coinciden (case sensitive)
     */
    public boolean coincideCon(String permisoNombre) {
        return this.nombrePermiso.equals(permisoNombre);
    }
}