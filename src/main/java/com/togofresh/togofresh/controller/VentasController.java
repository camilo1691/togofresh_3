package com.togofresh.togofresh.controller;

import com.togofresh.togofresh.model.Factura;
import com.togofresh.togofresh.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentasController {

    private final FacturaService facturaService;

    @Autowired
    public VentasController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public String listarFacturas(Model model) {
        List<Factura> facturas = facturaService.obtenerTodasFacturas();
        model.addAttribute("facturas", facturas);
        return "ventas";
    }

    //@GetMapping("/nueva")
    //public String mostrarFormularioNuevaFactura() {
    //    return "redirect:/factura.html";
    //}
}