package com.togofresh.togofresh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.togofresh.togofresh.dto.LoginRequest;
import com.togofresh.togofresh.dto.LoginResponse;
import com.togofresh.togofresh.model.Usuario;
import com.togofresh.togofresh.service.AuthService;
import com.togofresh.togofresh.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String RESET_PASSWORD_MSG = "Se ha enviado un enlace para resetear su contraseña";
    private static final String LOGOUT_MSG = "Sesión cerrada exitosamente";
    private static final String AUTH_SUCCESS_MSG = "Autenticación exitosa";
    private static final String INVALID_CREDENTIALS_MSG = "Credenciales incorrectas";
    private static final String SERVER_ERROR_MSG = "Error en el servidor";
    private static final String REQUEST_ERROR_MSG = "Error al procesar la solicitud";

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;




    public AuthController(AuthService authService, UsuarioService usuarioService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
    }
        

    // Versión para formularios HTML

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request) {
        try {
            Usuario usuario = authService.autenticarUsuario(username, password);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/menu-principal.html")
                    .build();

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/?error=bad_credentials")
                    .build();
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/?error=account_locked")
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/?error=server_error")
                    .build();
        }
    }



    // Versión original para API (JSON)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuario = authService.autenticarUsuario(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            );

            List<String> permisos = usuario.getRol().getPermisos().stream()
                .map(permiso -> permiso.getNombrePermiso())
                .toList();

            LoginResponse response = LoginResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .rol(usuario.getRol().getNombreRol())
                .permisos(permisos)
                .token(authService.generalTokenJWT(usuario))
                .mensaje(AUTH_SUCCESS_MSG)
                .success(true)
                .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(INVALID_CREDENTIALS_MSG, false));
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                .body(new LoginResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new LoginResponse(SERVER_ERROR_MSG, false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = extraerToken(request);
        if (token != null) {
            authService.invalidarToken(token);
        }
        return ResponseEntity.ok(LOGOUT_MSG);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> solicitarResetContrasena(@RequestParam String email) {
        try {
            usuarioService.resetearContrasenaPorEmail(email);
            return ResponseEntity.ok(RESET_PASSWORD_MSG);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(REQUEST_ERROR_MSG);
        }
    }

    @GetMapping("/check-session")
    public ResponseEntity<Boolean> verificarSesion(HttpServletRequest request) {
        String token = extraerToken(request);
        boolean valido = token != null && authService.validarToken(token);
        return ResponseEntity.ok(valido);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/user-info")
    public ResponseEntity<Usuario> obtenerInfoUsuario(@RequestParam Long idUsuario) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}