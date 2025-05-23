package com.togofresh.togofresh.dto;

import java.util.List;

public class LoginResponse {
    private Long idUsuario;
    private String username;
    private String email;
    private String nombreCompleto;
    private String rol;
    private List<String> permisos;
    private String token;
    private String tipoToken = "Bearer";
    private String mensaje;
    private boolean success;

    // Constructores
    public LoginResponse() {
        // Constructor vacío necesario para deserialización
    }

    // Constructor completo para éxito
    public LoginResponse(Long idUsuario, String username, String email, 
                       String nombreCompleto, String rol, List<String> permisos, 
                       String token, String mensaje, boolean success) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.permisos = permisos;
        this.token = token;
        this.mensaje = mensaje;
        this.success = success;
    }

    // Constructor simplificado para éxito
    public LoginResponse(Long idUsuario, String username, String email,
                       String nombreCompleto, String rol, List<String> permisos,
                       String token) {
        this(idUsuario, username, email, nombreCompleto, rol, permisos, token, 
             "Autenticación exitosa", true);
    }

    // Constructor para error
    public LoginResponse(String mensaje, boolean success) {
        this(null, null, null, null, null, null, null, mensaje, success);
    }

    // Constructor básico para éxito (sin permisos)
    public LoginResponse(Long idUsuario, String username, String email,
                       String nombreCompleto, String rol, String token,
                       String mensaje, boolean success) {
        this(idUsuario, username, email, nombreCompleto, rol, null, token, mensaje, success);
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Método toString() seguro
    @Override
    public String toString() {
        return "LoginResponse{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rol='" + rol + '\'' +
                ", permisos=" + (permisos != null ? permisos.size() + " permisos" : "null") +
                ", token='" + (token != null ? "[PROTEGIDO]" : "null") + '\'' +
                ", tipoToken='" + tipoToken + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", success=" + success +
                '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long idUsuario;
        private String username;
        private String email;
        private String nombreCompleto;
        private String rol;
        private List<String> permisos;
        private String token;
        private String mensaje;
        private boolean success;

        public Builder idUsuario(Long idUsuario) {
            this.idUsuario = idUsuario;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public Builder rol(String rol) {
            this.rol = rol;
            return this;
        }

        public Builder permisos(List<String> permisos) {
            this.permisos = permisos;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder mensaje(String mensaje) {
            this.mensaje = mensaje;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(idUsuario, username, email, nombreCompleto, 
                                   rol, permisos, token, 
                                   mensaje != null ? mensaje : "Autenticación exitosa", 
                                   success);
        }
    }
}