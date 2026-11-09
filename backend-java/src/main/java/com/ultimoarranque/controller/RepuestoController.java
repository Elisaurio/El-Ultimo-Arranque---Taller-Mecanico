package com.ultimoarranque.controller;
import com.ultimoarranque.model.Repuesto;
import com.ultimoarranque.service.RepuestoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/repuestos")
public class RepuestoController {
    private final RepuestoService servicio;
    public RepuestoController(RepuestoService servicio) {this.servicio=servicio;}
    @GetMapping
    public String listar(Model model) {model.addAttribute("registros",servicio.listar());return "repuestos/listar";}
    @GetMapping("/nuevo")
    public String nuevo(Model model) {model.addAttribute("registro",new Repuesto());return "repuestos/formulario";}
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {model.addAttribute("registro",servicio.obtener(id));return "repuestos/formulario";}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());return "redirect:/repuestos";}
    }
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("registro") Repuesto registro,BindingResult result,Model model,RedirectAttributes flash) {
        if(result.hasErrors()) {return "repuestos/formulario";}
        try {servicio.guardar(registro);flash.addFlashAttribute("exito","Repuesto guardado correctamente");return "redirect:/repuestos";}
        catch(IllegalArgumentException ex) {result.reject("registro.error",ex.getMessage());return "repuestos/formulario";}
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable long id,RedirectAttributes flash) {
        try {servicio.eliminar(id);flash.addFlashAttribute("exito","Registro eliminado");}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/repuestos";
    }
    @GetMapping("/bajo-inventario")
    public String bajo(Model model) {model.addAttribute("registros",servicio.inventarioBajo());return "repuestos/listar";}
    @GetMapping("/{id}/abastecer")
    public String abastecerForm(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {model.addAttribute("registro",servicio.obtener(id));return "repuestos/abastecer";}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());return "redirect:/repuestos";}
    }
    @PostMapping("/{id}/abastecer")
    public String abastecer(@PathVariable long id,@RequestParam int cantidad,RedirectAttributes flash) {
        try {servicio.abastecer(id,cantidad);flash.addFlashAttribute("exito","Inventario actualizado");}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());return "redirect:/repuestos/"+id+"/abastecer";}
        return "redirect:/repuestos";
    }
}
