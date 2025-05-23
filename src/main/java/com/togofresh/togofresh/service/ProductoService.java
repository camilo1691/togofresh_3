// src/main/java/com/togofresh/togofresh/service/ProductoService.java
package com.togofresh.togofresh.service;

import com.togofresh.togofresh.model.Producto;
import com.togofresh.togofresh.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> buscarProductos(String query) {
        return productoRepository.findByNombreContainingIgnoreCase(query);
    }
    
    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    
    // Otros métodos CRUD...
}