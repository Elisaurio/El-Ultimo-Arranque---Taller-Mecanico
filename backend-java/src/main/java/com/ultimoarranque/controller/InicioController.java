package com.ultimoarranque.controller;

import com.ultimoarranque.service.ClienteService;
import com.ultimoarranque.service.OrdenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    private final ClienteService clientes;
    private final OrdenService ordenes;
    public InicioController(ClienteService clientes,OrdenService ordenes) {
        this.clientes=clientes;this.ordenes=ordenes;
    }
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("nombreTaller","El Último Arranque");
        model.addAttribute("totalClientes",clientes.contar());
        model.addAttribute("totalOrdenes",ordenes.contar());
        return "inicio";
    }
}
