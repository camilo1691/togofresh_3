package com.togofresh.togofresh.controller;

import com.togofresh.togofresh.model.Cliente;
import com.togofresh.togofresh.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Cliente>> buscarClientes(@RequestParam String q) {
        List<Cliente> clientes = clienteService.buscarClientes(q);
        return ResponseEntity.ok(clientes);
    }
}