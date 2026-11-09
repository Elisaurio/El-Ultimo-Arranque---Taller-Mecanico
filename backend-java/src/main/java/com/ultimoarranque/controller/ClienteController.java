package com.ultimoarranque.controller;

import com.ultimoarranque.model.Cliente;
import com.ultimoarranque.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService servicio;
    public ClienteController(ClienteService servicio) { this.servicio = servicio; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", servicio.listar());
        return "clientes/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id, Model model, RedirectAttributes flash) {
        try { model.addAttribute("cliente", servicio.obtener(id)); return "clientes/formulario"; }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("error", ex.getMessage()); return "redirect:/clientes"; }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable long id, Model model, RedirectAttributes flash) {
        try { model.addAttribute("cliente", servicio.obtener(id)); return "clientes/detalle"; }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("error", ex.getMessage()); return "redirect:/clientes"; }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) return "clientes/formulario";
        try { servicio.guardar(cliente); flash.addFlashAttribute("exito", "Cliente guardado correctamente"); return "redirect:/clientes"; }
        catch (IllegalArgumentException ex) { result.reject("cliente.error", ex.getMessage()); return "clientes/formulario"; }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable long id, RedirectAttributes flash) {
        try { servicio.eliminar(id); flash.addFlashAttribute("exito", "Cliente eliminado"); }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/clientes";
    }
}
