package com.togofresh.togofresh.service;

import com.togofresh.togofresh.model.Rol;
import com.togofresh.togofresh.model.Usuario;
import com.togofresh.togofresh.repository.RolRepository;
import com.togofresh.togofresh.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    // Constantes para mensajes y valores repetidos
    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String ROL_NO_ENCONTRADO = "Rol no encontrado";
    private static final String CONTRASENA_TEMPORAL = "Temp@GoFresh123"; // Contraseña temporal segura
    private static final String EMAIL_NO_ENCONTRADO = "Usuario no encontrado con email: ";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                         RolRepository rolRepository,
                         PasswordEncoder passwordEncoder,
                         EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /* ==================== MÉTODOS CRUD ==================== */

    @Transactional
    public Usuario crearUsuario(Usuario usuario, Long idRol) {
        validarUnicidadUsuario(usuario.getEmail(), usuario.getUsername());
        
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException(ROL_NO_ENCONTRADO));

        usuario.setRol(rol);
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        usuario.setActivo(true);
        usuario.setIntentosFallidos(0);
        usuario.setCuentaBloqueada(false);
        usuario.setFechaDesbloqueo(null);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> listarUsuariosPaginados(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));

        // Actualizar solo campos permitidos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void cambiarEstadoUsuario(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    /* ==================== GESTIÓN DE ROLES ==================== */

    @Transactional
    public Usuario cambiarRolUsuario(Long idUsuario, Long idNuevoRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));
        
        Rol nuevoRol = rolRepository.findById(idNuevoRol)
                .orElseThrow(() -> new RuntimeException(ROL_NO_ENCONTRADO));

        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }

    /* ==================== GESTIÓN DE CONTRASEÑAS ==================== */

    @Transactional
    public void cambiarContrasena(Long idUsuario, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getPasswordHash())) {
            throw new RuntimeException("La contraseña actual no es válida");
        }

        usuario.setPasswordHash(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void resetearContrasena(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));

        usuario.setPasswordHash(passwordEncoder.encode(CONTRASENA_TEMPORAL));
        usuarioRepository.save(usuario);
    }

    /**
     * Resetea la contraseña de un usuario por email y envía una nueva contraseña temporal
     */
    @Transactional
    public void resetearContrasenaPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(EMAIL_NO_ENCONTRADO + email));

        // Generar contraseña temporal más segura que combine letras, números y caracteres especiales
        String nuevaContrasenaTemporal = generarContrasenaTemporalSegura();
        String contrasenaEncriptada = passwordEncoder.encode(nuevaContrasenaTemporal);

        // Actualizar contraseña en la base de datos
        usuario.setPasswordHash(contrasenaEncriptada);
        usuarioRepository.save(usuario);

        // Enviar email con la nueva contraseña
        if (emailService != null) {
            emailService.enviarEmailResetContrasena(
                    usuario.getEmail(),
                    "Restablecimiento de contraseña - ToGoFresh",
                    "Hola " + usuario.getNombre() + ",\n\n" +
                    "Hemos recibido una solicitud para restablecer tu contraseña.\n\n" +
                    "Tu nueva contraseña temporal es: " + nuevaContrasenaTemporal + "\n\n" +
                    "Por seguridad, te recomendamos cambiar esta contraseña después de iniciar sesión.\n\n" +
                    "Si no solicitaste este cambio, por favor contacta con nuestro soporte.\n\n" +
                    "Saludos,\nEquipo ToGoFresh");
        }
    }

    /* ==================== BLOQUEO/DESBLOQUEO ==================== */

    @Transactional
    public void bloquearUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));

        usuario.setCuentaBloqueada(true);
        usuario.setFechaDesbloqueo(null); // Bloqueo indefinido
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desbloquearUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));

        usuario.setCuentaBloqueada(false);
        usuario.setFechaDesbloqueo(null);
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void bloquearUsuarioTemporalmente(Long idUsuario, int horas) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));

        usuario.setCuentaBloqueada(true);
        usuario.setFechaDesbloqueo(LocalDateTime.now().plusHours(horas));
        usuarioRepository.save(usuario);
    }

    /* ==================== MÉTODOS DE BÚSQUEDA ==================== */

    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Usuario> buscarPorRol(Long idRol) {
        return usuarioRepository.findByRol_IdRol(idRol);
    }

    public List<Usuario> buscarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /* ==================== VALIDACIONES ==================== */

    private void validarUnicidadUsuario(String email, String username) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
    }

    public boolean verificarContrasena(Long idUsuario, String contrasena) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));
        return passwordEncoder.matches(contrasena, usuario.getPasswordHash());
    }

    /* ==================== MÉTODOS PRIVADOS DE AYUDA ==================== */

    /**
     * Genera una contraseña temporal segura que incluye letras, números y caracteres especiales
     */
    private String generarContrasenaTemporalSegura() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder(10);
        
        // Asegurar al menos un carácter de cada tipo
        sb.append((char) ('A' + (int) (Math.random() * 26))); // Mayúscula
        sb.append((char) ('a' + (int) (Math.random() * 26))); // Minúscula
        sb.append((char) ('0' + (int) (Math.random() * 10))); // Número
        sb.append("!@#$%^&*".charAt((int) (Math.random() * 8))); // Carácter especial
        
        // Completar con caracteres aleatorios
        for (int i = 0; i < 6; i++) {
            sb.append(caracteres.charAt((int) (Math.random() * caracteres.length())));
        }
        
        // Mezclar los caracteres para mayor seguridad
        return sb.toString().chars()
                .mapToObj(c -> (char) c)
                .collect(StringBuilder::new, (sb2, c) -> sb2.insert((int) (Math.random() * (sb2.length() + 1)), c), StringBuilder::append)
                .toString();
    }
}