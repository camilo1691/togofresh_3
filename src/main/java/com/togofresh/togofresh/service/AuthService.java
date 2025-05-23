package com.togofresh.togofresh.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togofresh.togofresh.model.Usuario;
import com.togofresh.togofresh.repository.UsuarioRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConcurrentHashMap<String, Boolean> tokenBlacklist = new ConcurrentHashMap<>();
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;
    
    private Key jwtSecretKey;

    // Constantes de configuración
    private static final int MAX_INTENTOS_FALLIDOS = 3;
    private static final int TIEMPO_BLOQUEO_HORAS = 1;
    private static final String MSG_USUARIO_NO_ENCONTRADO = "Credenciales inválidas";
    private static final String MSG_CUENTA_BLOQUEADA_TEMP = "Cuenta bloqueada temporalmente por %d hora(s). Intente más tarde";
    private static final String MSG_CUENTA_BLOQUEADA_PERM = "Cuenta bloqueada. Contacte al administrador";
    private static final String MSG_CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    public AuthService(UsuarioRepository usuarioRepository, 
                     PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void initialize() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("La propiedad jwt.secret no está configurada en application.properties");
        }
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT para el usuario autenticado
     */
    public String generalTokenJWT(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .claim("id", usuario.getIdUsuario())
                .claim("rol", usuario.getRol().getNombreRol())
                .claim("email", usuario.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Autentica un usuario con username/email y contraseña
     */
    public Usuario autenticarUsuario(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username/email y password son requeridos");
        }

        Usuario usuario = usuarioRepository.findByUsernameOrEmail(usernameOrEmail)
            .orElseThrow(() -> new UsernameNotFoundException(MSG_USUARIO_NO_ENCONTRADO));

            System.out.println("-------- "+ password);
            System.out.println("-------- "+ usuario.getPasswordHash());

        verificarBloqueoCuenta(usuario);

        System.out.println("Ingresado por el usuario:");
        System.out.println("username: [" + usernameOrEmail + "]");
        System.out.println("password: [" + password + "]");
        System.out.println("Hash en BD: [" + usuario.getPasswordHash() + "]");
        System.out.println("¿Coincide?: " + passwordEncoder.matches(password, usuario.getPasswordHash()));

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            manejarIntentoFallido(usuario);
            throw new BadCredentialsException(MSG_CREDENCIALES_INVALIDAS);
        }

        reiniciarIntentosFallidos(usuario);
        return usuario;
    }

    /**
     * Carga UserDetails para Spring Security
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(MSG_USUARIO_NO_ENCONTRADO));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRol().getNombreRol())
                .accountLocked(!usuario.isAccountNonLocked())
                .disabled(!usuario.getActivo())
                .build();
    }

    /**
     * Verifica si la cuenta está bloqueada
     */
    private void verificarBloqueoCuenta(Usuario usuario) {
        if (usuario.isCuentaBloqueada()) {
            LocalDateTime fechaDesbloqueo = usuario.getFechaDesbloqueo();
            if (fechaDesbloqueo != null && LocalDateTime.now().isBefore(fechaDesbloqueo)) {
                long horasRestantes = java.time.Duration.between(LocalDateTime.now(), fechaDesbloqueo).toHours();
                throw new LockedException(String.format(MSG_CUENTA_BLOQUEADA_TEMP, horasRestantes + 1));
            }
            throw new LockedException(MSG_CUENTA_BLOQUEADA_PERM);
        }
    }

    /**
     * Maneja un intento fallido de autenticación
     */
    private void manejarIntentoFallido(Usuario usuario) {
        usuario.incrementarIntentosFallidos();
        
        if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
            usuario.bloquearCuentaTemporalmente(TIEMPO_BLOQUEO_HORAS);
        }
        
        usuarioRepository.save(usuario);
    }

    /**
     * Reinicia los intentos fallidos después de un login exitoso
     */
    private void reiniciarIntentosFallidos(Usuario usuario) {
        if (usuario.getIntentosFallidos() > 0 || usuario.isCuentaBloqueada()) {
            usuario.resetearIntentosFallidos();
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Desbloquea administrativamente una cuenta
     */
    public void desbloquearUsuarioAdministrativo(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        usuario.resetearIntentosFallidos();
        usuarioRepository.save(usuario);
    }

    /**
     * Invalida un token JWT (lo añade a la lista negra)
     */
    public void invalidarToken(String token) {
        if (token != null && !token.isEmpty()) {
            tokenBlacklist.put(token, true);
        }
    }

    /**
     * Verifica si un token JWT es válido (no está en la lista negra)
     */
    public boolean validarToken(String token) {
        return token != null && !token.isEmpty() && !tokenBlacklist.containsKey(token);
    }

    /**
     * Extrae el username del token JWT
     */
    public String getUsernameFromToken(String token) {
        if (!validarToken(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Valida un token JWT contra un usuario específico
     */
    public boolean validarTokenParaUsuario(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return username.equals(tokenUsername);
        } catch (Exception e) {
            return false;
        }
    }
}