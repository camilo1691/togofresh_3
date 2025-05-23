package com.togofresh.togofresh.repository;

import com.togofresh.togofresh.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // -------------------- Consultas Básicas --------------------
    Permiso findByNombrePermiso(String nombrePermiso);
    
    List<Permiso> findByDescripcionContainingIgnoreCase(String keyword);

    // -------------------- Consultas de Relaciones (Roles-Permisos) --------------------
    @Query("SELECT p FROM Permiso p JOIN p.roles r WHERE r.idRol = :idRol")
    Set<Permiso> findPermisosByRolId(@Param("idRol") Long idRol);

    @Query("SELECT p FROM Permiso p WHERE NOT EXISTS " +"(SELECT 1 FROM p.roles r WHERE r.idRol = :idRol)")
    Set<Permiso> findPermisosNoAsignadosARol(@Param("idRol") Long idRol);

    // -------------------- Consultas de Asignación/Remoción --------------------
    @Modifying
    @Query(value = "INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (:idRol, :idPermiso)", nativeQuery = true)
    void asignarPermisoARol(@Param("idRol") Long idRol, @Param("idPermiso") Long idPermiso);

    @Modifying
    @Query(value = "DELETE FROM roles_permisos WHERE id_rol = :idRol AND id_permiso = :idPermiso", nativeQuery = true)
    void removerPermisoDeRol(@Param("idRol") Long idRol, @Param("idPermiso") Long idPermiso);

    // -------------------- Consultas de Auditoría --------------------
    @Query("SELECT p FROM Permiso p WHERE p.fechaCreacion >= :fechaDesde")
    List<Permiso> findPermisosCreadosDesde(@Param("fechaDesde") LocalDateTime fechaDesde);

    // -------------------- Consultas para Reportes --------------------
    @Query(value = """
        SELECT p.nombre_permiso, COUNT(rp.id_rol) as total_roles 
        FROM permisos p
        LEFT JOIN roles_permisos rp ON p.id_permiso = rp.id_permiso
        GROUP BY p.nombre_permiso
        """, nativeQuery = true)
    List<Object[]> contarRolesPorPermiso();

    // -------------------- Validaciones --------------------
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permiso p WHERE p.nombrePermiso = :nombre")
    boolean existePorNombrePermiso(@Param("nombre") String nombrePermiso);
}