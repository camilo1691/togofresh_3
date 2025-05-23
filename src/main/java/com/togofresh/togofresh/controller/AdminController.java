package com.togofresh.togofresh.controller;

import com.togofresh.togofresh.dto.UsuarioDTO;
import com.togofresh.togofresh.model.Usuario;
import com.togofresh.togofresh.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /* ================ ENDPOINTS CRUD ================ */

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(
                convertirAEntidad(usuarioDTO),
                usuarioDTO.getIdRol()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Usuario>> listarUsuarios(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarUsuariosPaginados(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(
                id, 
                convertirAEntidad(usuarioDTO)
            );
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id) {
        try {
            usuarioService.cambiarEstadoUsuario(id, false);
            return ResponseEntity.ok("Usuario desactivado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* ================ GESTIÓN DE ESTADOS ================ */

    @PostMapping("/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable Long id) {
        try {
            usuarioService.cambiarEstadoUsuario(id, true);
            return ResponseEntity.ok("Usuario activado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/bloquear")
    public ResponseEntity<?> bloquearUsuario(@PathVariable Long id) {
        try {
            usuarioService.bloquearUsuario(id);
            return ResponseEntity.ok("Usuario bloqueado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<?> desbloquearUsuario(@PathVariable Long id) {
        try {
            usuarioService.desbloquearUsuario(id);
            return ResponseEntity.ok("Usuario desbloqueado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* ================ GESTIÓN DE ROLES ================ */

    @PutMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRolUsuario(
            @PathVariable Long id,
            @RequestParam Long nuevoRolId) {
        try {
            Usuario usuario = usuarioService.cambiarRolUsuario(id, nuevoRolId);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* ================ GESTIÓN DE CONTRASEÑAS ================ */

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetearContrasena(@PathVariable Long id) {
        try {
            usuarioService.resetearContrasena(id);
            return ResponseEntity.ok("Contraseña reseteada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* ================ MÉTODOS AUXILIARES ================ */

    private Usuario convertirAEntidad(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setUsername(dto.getUsername());
        usuario.setTelefono(dto.getTelefono());
        usuario.setPasswordHash(dto.getPassword());
        return usuario;
    }
}