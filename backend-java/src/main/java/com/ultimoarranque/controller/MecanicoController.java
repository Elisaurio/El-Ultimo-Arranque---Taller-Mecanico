package com.ultimoarranque.controller;
import com.ultimoarranque.model.Mecanico;
import com.ultimoarranque.service.MecanicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mecanicos")
public class MecanicoController {
    private final MecanicoService servicio;
    public MecanicoController(MecanicoService servicio) {this.servicio=servicio;}
    @GetMapping
    public String listar(Model model) {model.addAttribute("registros",servicio.listar());return "mecanicos/listar";}
    @GetMapping("/nuevo")
    public String nuevo(Model model) {model.addAttribute("registro",new Mecanico());return "mecanicos/formulario";}
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {model.addAttribute("registro",servicio.obtener(id));return "mecanicos/formulario";}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());return "redirect:/mecanicos";}
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("registro") Mecanico registro,BindingResult result,Model model,RedirectAttributes flash) {
        if(result.hasErrors()) {return "mecanicos/formulario";}
        try {servicio.guardar(registro);flash.addFlashAttribute("exito","Mecanico guardado correctamente");return "redirect:/mecanicos";}
        catch(IllegalArgumentException ex) {result.reject("registro.error",ex.getMessage());return "mecanicos/formulario";}
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable long id,RedirectAttributes flash) {
        try {servicio.eliminar(id);flash.addFlashAttribute("exito","Registro eliminado");}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/mecanicos";
    }
}
