package com.togofresh.togofresh.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras y espacios")
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^[0-9]{7,20}$", message = "El teléfono debe contener solo números y tener entre 7 y 20 dígitos")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Solo se permiten letras, números, puntos, guiones bajos y medios")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "token_reset", length = 255)
    private String tokenReset;

    @Column(name = "token_reset_expira")
    private LocalDateTime tokenResetExpira;

    @Column(name = "activo", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "fecha_desbloqueo")
    private LocalDateTime fechaDesbloqueo;

    @Min(value = 0, message = "Los intentos fallidos no pueden ser negativos")
    @Column(name = "intentos_fallidos", columnDefinition = "INT DEFAULT 0")
    private int intentosFallidos = 0;

    @Column(name = "cuenta_bloqueada", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean cuentaBloqueada = false;

    // -------------------- Constructores --------------------
    public Usuario() {}

    public Usuario(Rol rol, String nombre, String apellido, String email, String username, String passwordHash) {
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }


    // -------------------- Getters y Setters --------------------
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTokenReset() {
        return tokenReset;
    }

    public void setTokenReset(String tokenReset) {
        this.tokenReset = tokenReset;
    }

    public LocalDateTime getTokenResetExpira() {
        return tokenResetExpira;
    }

    public void setTokenResetExpira(LocalDateTime tokenResetExpira) {
        this.tokenResetExpira = tokenResetExpira;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public LocalDateTime getFechaDesbloqueo() {
        return fechaDesbloqueo;
    }

    public void setFechaDesbloqueo(LocalDateTime fechaDesbloqueo) {
        this.fechaDesbloqueo = fechaDesbloqueo;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public boolean isCuentaBloqueada() {
        return cuentaBloqueada;
    }

    public void setCuentaBloqueada(boolean cuentaBloqueada) {
        this.cuentaBloqueada = cuentaBloqueada;
    }

    // -------------------- Validaciones --------------------
    @Transient
    public boolean isAccountNonLocked() {
        return !cuentaBloqueada || (fechaDesbloqueo != null && LocalDateTime.now().isAfter(fechaDesbloqueo));
    }

    @Transient
    public boolean isTokenResetValido() {
        return tokenReset != null && tokenResetExpira != null && 
               LocalDateTime.now().isBefore(tokenResetExpira);
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
    public void incrementarIntentosFallidos() {
        this.intentosFallidos++;
    }

    public void resetearIntentosFallidos() {
        this.intentosFallidos = 0;
        this.cuentaBloqueada = false;
        this.fechaDesbloqueo = null;
    }

    public void bloquearCuentaTemporalmente(int horasBloqueo) {
        this.cuentaBloqueada = true;
        this.fechaDesbloqueo = LocalDateTime.now().plusHours(horasBloqueo);
    }

    public void generarTokenReset() {
        this.tokenReset = java.util.UUID.randomUUID().toString();
        this.tokenResetExpira = LocalDateTime.now().plusHours(24);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", cuentaBloqueada=" + cuentaBloqueada +
                '}';
    }
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Asumiendo que Usuario tiene una relación con Rol
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.getRol().getNombreRol()));
        
        // Si tienes múltiples roles/autoridades:
        // return this.roles.stream()
        //     .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombre()))
        //     .collect(Collectors.toList());
    }
}