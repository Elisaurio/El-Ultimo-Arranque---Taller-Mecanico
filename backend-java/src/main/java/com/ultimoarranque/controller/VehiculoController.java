package com.ultimoarranque.controller;
import com.ultimoarranque.model.Vehiculo;
import com.ultimoarranque.service.VehiculoService;
import com.ultimoarranque.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {
    private final VehiculoService servicio;
    private final ClienteService clientes;
    public VehiculoController(VehiculoService servicio, ClienteService clientes) {this.servicio=servicio;this.clientes=clientes;}
    @GetMapping
    public String listar(Model model) {model.addAttribute("registros",servicio.listar());return "vehiculos/listar";}
    @GetMapping("/nuevo")
    public String nuevo(Model model) {model.addAttribute("registro",new Vehiculo());model.addAttribute("clientes",clientes.listar());return "vehiculos/formulario";}
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {model.addAttribute("registro",servicio.obtener(id));model.addAttribute("clientes",clientes.listar());return "vehiculos/formulario";}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());return "redirect:/vehiculos";}
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("registro") Vehiculo registro,BindingResult result,Model model,RedirectAttributes flash) {
        if(result.hasErrors()) {model.addAttribute("clientes",clientes.listar());return "vehiculos/formulario";}
        try {servicio.guardar(registro);flash.addFlashAttribute("exito","Vehiculo guardado correctamente");return "redirect:/vehiculos";}
        catch(IllegalArgumentException ex) {result.reject("registro.error",ex.getMessage());model.addAttribute("clientes",clientes.listar());return "vehiculos/formulario";}
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable long id,RedirectAttributes flash) {
        try {servicio.eliminar(id);flash.addFlashAttribute("exito","Registro eliminado");}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/vehiculos";
    }
    @GetMapping("/{id}")
    public String detalle(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {
            model.addAttribute("registro",servicio.obtener(id));
            model.addAttribute("historial",servicio.historial(id));
            return "vehiculos/detalle";
        } catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());return "redirect:/vehiculos";}
    }
}
