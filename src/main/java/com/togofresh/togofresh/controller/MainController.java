package com.togofresh.togofresh.controller;  // Asegúrate que coincida con tu estructura de paquetes

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html"; // Redirige a tu página principal
    }

    @GetMapping("/index")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/error")
    public String handleError() {
        return "forward:/index.html"; // Manejo básico de errores
    }
}