package com.togofresh.togofresh.repository;

import com.togofresh.togofresh.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Métodos básicos de búsqueda
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Busca un usuario por su nombre de usuario o email
     * @param usernameOrEmail Nombre de usuario o dirección de email
     * @return Optional con el usuario si existe
     */
    @Query("SELECT u FROM Usuario u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<Usuario> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    // Métodos de búsqueda por criterios
    List<Usuario> findByRol_IdRol(Long idRol);
    List<Usuario> findByActivo(Boolean activo);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByActivoTrue();

    // Métodos de actualización
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoLogin = :fecha WHERE u.idUsuario = :id")
    int actualizarUltimoLogin(@Param("id") Long idUsuario, @Param("fecha") LocalDateTime fecha);

    @Modifying
    @Query("UPDATE Usuario u SET u.activo = :activo, u.fechaDesbloqueo = :fecha, u.intentosFallidos = :intentos WHERE u.idUsuario = :id")
    int actualizarEstadoUsuario(
        @Param("id") Long idUsuario,
        @Param("activo") Boolean activo,
        @Param("fecha") LocalDateTime fechaDesbloqueo,
        @Param("intentos") int intentosFallidos
    );

    // Métodos para reportes y estadísticas
    @Query(value = """
        SELECT u.id_usuario, u.nombre, u.email, u.activo, r.nombre_rol as rol,
               COUNT(v.id_venta) as ventas_realizadas
        FROM usuarios u
        JOIN roles r ON u.id_rol = r.id_rol
        LEFT JOIN ventas v ON u.id_usuario = v.id_usuario
        WHERE u.fecha_creacion BETWEEN :inicio AND :fin
        GROUP BY u.id_usuario, u.nombre, u.email, u.activo, r.nombre_rol
        """, nativeQuery = true)
    List<Object[]> findUsuariosConEstadisticasPorPeriodo(
        @Param("inicio") LocalDateTime fechaInicio,
        @Param("fin") LocalDateTime fechaFin
    );

    // Métodos de validación
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    // Método para bloquear usuario después de intentos fallidos
    @Modifying
    @Query("UPDATE Usuario u SET u.cuentaBloqueada = true, " +
           "u.fechaDesbloqueo = :fechaDesbloqueo, " +
           "u.intentosFallidos = u.intentosFallidos + 1 " +
           "WHERE u.idUsuario = :id")
    int bloquearUsuarioTemporalmente(
        @Param("id") Long idUsuario,
        @Param("fechaDesbloqueo") LocalDateTime fechaDesbloqueo
    );

    // Método para resetear intentos fallidos
    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = 0, " +
           "u.cuentaBloqueada = false, " +
           "u.fechaDesbloqueo = null " +
           "WHERE u.idUsuario = :id")
    int resetearIntentosFallidos(@Param("id") Long idUsuario);
}