// src/main/java/com/togofresh/togofresh/repository/ProductoRepository.java
package com.togofresh.togofresh.repository;

import com.togofresh.togofresh.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoria(String categoria);
}