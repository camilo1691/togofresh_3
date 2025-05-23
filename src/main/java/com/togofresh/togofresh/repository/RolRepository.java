package com.togofresh.togofresh.repository;

import com.togofresh.togofresh.model.Rol;
import com.togofresh.togofresh.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /* -------------------- CONSULTAS BÁSICAS -------------------- */
    Optional<Rol> findByNombreRol(String nombreRol);
    
    List<Rol> findByDescripcionContainingIgnoreCase(String keyword);

    /* -------------------- RELACIONES CON USUARIOS -------------------- */
    @Query("SELECT r FROM Rol r JOIN r.usuarios u WHERE u.idUsuario = :usuarioId")
    Optional<Rol> findRolByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.idRol = :rolId")
    int countUsuariosByRolId(@Param("rolId") Long rolId);

    /* -------------------- GESTIÓN DE PERMISOS -------------------- */
    @Query("SELECT r.permisos FROM Rol r WHERE r.idRol = :rolId")
    Set<Permiso> findPermisosByRolId(@Param("rolId") Long rolId);

    @Modifying
    @Query(value = "INSERT INTO roles_permisos (id_rol, id_permiso, fecha_asignacion) VALUES (:rolId, :permisoId, CURRENT_TIMESTAMP)", nativeQuery = true)
    void asignarPermiso(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);

    @Modifying
    @Query(value = "DELETE FROM roles_permisos WHERE id_rol = :rolId AND id_permiso = :permisoId", nativeQuery = true)
    void revocarPermiso(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);

    /* -------------------- AUDITORÍA -------------------- */
    @Query("SELECT r FROM Rol r WHERE r.fechaCreacion BETWEEN :inicio AND :fin")
    List<Rol> findRolesCreadosEnPeriodo(
        @Param("inicio") LocalDateTime fechaInicio,
        @Param("fin") LocalDateTime fechaFin
    );

    /* -------------------- VALIDACIONES -------------------- */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Rol r WHERE r.nombreRol = :nombre")
    boolean existsByNombreRol(@Param("nombre") String nombreRol);

    @Query(value = "SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END " +
                   "FROM roles_permisos rp " +
                   "WHERE rp.id_rol = :rolId AND rp.id_permiso = :permisoId", nativeQuery = true)
    boolean tienePermisoAsignado(
        @Param("rolId") Long rolId,
        @Param("permisoId") Long permisoId
    );

    /* -------------------- CONSULTAS NATIVAS PARA REPORTES -------------------- */
    @Query(value = "SELECT r.nombre_rol, COUNT(u.id_usuario) as total_usuarios " +
                   "FROM roles r " +
                   "LEFT JOIN usuarios u ON r.id_rol = u.id_rol " +
                   "GROUP BY r.nombre_rol", nativeQuery = true)
    List<Object[]> contarUsuariosPorRol();

    @Query(value = "SELECT r.nombre_rol, p.nombre_permiso " +
                   "FROM roles r " +
                   "JOIN roles_permisos rp ON r.id_rol = rp.id_rol " +
                   "JOIN permisos p ON rp.id_permiso = p.id_permiso " +
                   "ORDER BY r.nombre_rol", nativeQuery = true)
    List<Object[]> listarPermisosPorRol();
}