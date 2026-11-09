package com.ultimoarranque.controller;

import com.ultimoarranque.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    private final ClienteService clienteService;
    public InicioController(ClienteService clienteService) { this.clienteService = clienteService; }
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("nombreTaller", "El Último Arranque");
        model.addAttribute("totalClientes", clienteService.contar());
        return "inicio";
    }
}
