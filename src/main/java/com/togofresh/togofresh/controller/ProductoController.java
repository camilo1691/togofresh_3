// src/main/java/com/togofresh/togofresh/controller/ProductoController.java
package com.togofresh.togofresh.controller;

import com.togofresh.togofresh.model.Producto;
import com.togofresh.togofresh.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @GetMapping("/search")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String q) {
        return ResponseEntity.ok(productoService.buscarProductos(q));
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }
}