package com.togofresh.togofresh.service;

import com.togofresh.togofresh.model.Cliente;
import com.togofresh.togofresh.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> buscarClientes(String query) {
        return clienteRepository.findByNombreContainingIgnoreCase(query);
    }
}