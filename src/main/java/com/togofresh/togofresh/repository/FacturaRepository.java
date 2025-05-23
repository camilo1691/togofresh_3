package com.togofresh.togofresh.repository;

import com.togofresh.togofresh.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findAllByOrderByFechaCreacionDesc();
}