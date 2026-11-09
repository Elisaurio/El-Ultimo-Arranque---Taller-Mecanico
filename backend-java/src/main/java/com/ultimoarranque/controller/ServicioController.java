package com.ultimoarranque.controller;
import com.ultimoarranque.model.Servicio;
import com.ultimoarranque.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios")
public class ServicioController {
    private final ServicioService servicio;
    public ServicioController(ServicioService servicio) {this.servicio=servicio;}
    @GetMapping
    public String listar(Model model) {model.addAttribute("registros",servicio.listar());return "servicios/listar";}
    @GetMapping("/nuevo")
    public String nuevo(Model model) {model.addAttribute("registro",new Servicio());return "servicios/formulario";}
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {model.addAttribute("registro",servicio.obtener(id));return "servicios/formulario";}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());return "redirect:/servicios";}
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("registro") Servicio registro,BindingResult result,Model model,RedirectAttributes flash) {
        if(result.hasErrors()) {return "servicios/formulario";}
        try {servicio.guardar(registro);flash.addFlashAttribute("exito","Servicio guardado correctamente");return "redirect:/servicios";}
        catch(IllegalArgumentException ex) {result.reject("registro.error",ex.getMessage());return "servicios/formulario";}
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable long id,RedirectAttributes flash) {
        try {servicio.eliminar(id);flash.addFlashAttribute("exito","Registro eliminado");}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/servicios";
    }
}
