package com.togofresh.togofresh.service;

import com.togofresh.togofresh.model.Factura;
import com.togofresh.togofresh.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    @Autowired
    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> obtenerTodasFacturas() {
        return facturaRepository.findAllByOrderByFechaCreacionDesc();
    }
}