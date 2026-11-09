package com.ultimoarranque.controller;

import com.ultimoarranque.service.ClienteService;
import com.ultimoarranque.service.OrdenService;
import com.ultimoarranque.service.VehiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final ClienteService clientes;
    private final OrdenService ordenes;
    private final VehiculoService vehiculos;

    public InicioController(ClienteService clientes, OrdenService ordenes, VehiculoService vehiculos) {
        this.clientes = clientes;
        this.ordenes = ordenes;
        this.vehiculos = vehiculos;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalClientes", clientes.contar());
        model.addAttribute("totalOrdenes", ordenes.contar());
        model.addAttribute("totalVehiculos", vehiculos.listar().size());
        return "inicio";
    }
}
