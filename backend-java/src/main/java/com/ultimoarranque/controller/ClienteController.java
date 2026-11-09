package com.ultimoarranque.controller;

import com.ultimoarranque.model.Cliente;
import com.ultimoarranque.model.Vehiculo;
import com.ultimoarranque.service.ClienteService;
import com.ultimoarranque.service.VehiculoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService servicio;
    private final VehiculoService vehiculos;
    private final Validator validator;

    public ClienteController(ClienteService servicio, VehiculoService vehiculos, Validator validator) {
        this.servicio=servicio;this.vehiculos=vehiculos;this.validator=validator;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes",servicio.listar());
        return "clientes/listar";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente",new Cliente());
        model.addAttribute("vehiculo",new Vehiculo());
        model.addAttribute("conVehiculo",false);
        return "clientes/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {
            model.addAttribute("cliente",servicio.obtener(id));
            model.addAttribute("vehiculo",new Vehiculo());
            model.addAttribute("conVehiculo",false);
            return "clientes/formulario";
        } catch(IllegalArgumentException ex) {
            flash.addFlashAttribute("error",ex.getMessage());return "redirect:/clientes";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {
            model.addAttribute("cliente",servicio.obtener(id));
            model.addAttribute("vehiculos",vehiculos.porCliente(id));
            return "clientes/detalle";
        } catch(IllegalArgumentException ex) {
            flash.addFlashAttribute("error",ex.getMessage());return "redirect:/clientes";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result,
            @RequestParam(defaultValue="false") boolean conVehiculo,
            @RequestParam(required=false) String placaVehiculo,
            @RequestParam(required=false) String marcaVehiculo,
            @RequestParam(required=false) String modeloVehiculo,
            @RequestParam(required=false) String anioVehiculo,
            @RequestParam(required=false) String colorVehiculo,
            Model model,RedirectAttributes flash) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placaVehiculo);
        vehiculo.setMarca(marcaVehiculo);
        vehiculo.setModelo(modeloVehiculo);
        vehiculo.setColor(colorVehiculo);
        if (conVehiculo && cliente.getId()==null) {
            if (anioVehiculo != null && !anioVehiculo.isBlank()) {
                try { vehiculo.setAnio(Integer.valueOf(anioVehiculo)); }
                catch(NumberFormatException ex) { result.reject("vehiculo.anio", "Año del vehículo inválido"); }
            }
            vehiculo.setClienteId(1L);
            Set<ConstraintViolation<Vehiculo>> errores=validator.validate(vehiculo);
            for (ConstraintViolation<Vehiculo> e : errores) result.reject("vehiculo.error", "Vehículo: " + e.getMessage());
            vehiculo.setClienteId(null);
        }
        if (result.hasErrors()) {
            model.addAttribute("vehiculo",vehiculo);
            model.addAttribute("conVehiculo",conVehiculo);
            return "clientes/formulario";
        }
        try {
            if (conVehiculo && cliente.getId()==null) servicio.guardarConVehiculo(cliente,vehiculo);
            else servicio.guardar(cliente);
            flash.addFlashAttribute("exito", "Cliente guardado correctamente");
            return "redirect:/clientes/" + cliente.getId();
        } catch(IllegalArgumentException ex) {
            result.reject("cliente.error",ex.getMessage());
            model.addAttribute("vehiculo",vehiculo);
            model.addAttribute("conVehiculo",conVehiculo);
            return "clientes/formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable long id,RedirectAttributes flash) {
        try {servicio.eliminar(id);flash.addFlashAttribute("exito","Cliente eliminado");}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/clientes";
    }
}
