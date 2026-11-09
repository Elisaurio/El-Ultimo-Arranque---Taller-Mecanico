package com.ultimoarranque.controller;

import com.ultimoarranque.dto.CrearOrdenForm;
import com.ultimoarranque.model.EstadoOrden;
import com.ultimoarranque.model.OrdenTrabajo;
import com.ultimoarranque.service.MecanicoService;
import com.ultimoarranque.service.OrdenService;
import com.ultimoarranque.service.RepuestoService;
import com.ultimoarranque.service.ServicioService;
import com.ultimoarranque.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ordenes")
public class OrdenController {
    private final OrdenService ordenes;
    private final VehiculoService vehiculos;
    private final MecanicoService mecanicos;
    private final ServicioService servicios;
    private final RepuestoService repuestos;

    public OrdenController(OrdenService ordenes,VehiculoService vehiculos,MecanicoService mecanicos,
            ServicioService servicios,RepuestoService repuestos) {
        this.ordenes=ordenes;
        this.vehiculos=vehiculos;
        this.mecanicos=mecanicos;
        this.servicios=servicios;
        this.repuestos=repuestos;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ordenes",ordenes.listar());
        return "ordenes/listar";
    }

    private void opciones(Model model) {
        model.addAttribute("vehiculos",vehiculos.listar());
        model.addAttribute("mecanicos",mecanicos.listar());
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("registro",new CrearOrdenForm());
        opciones(model);
        return "ordenes/crear";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("registro") CrearOrdenForm form,
            BindingResult result,Model model,RedirectAttributes flash) {
        if (result.hasErrors()) { opciones(model);return "ordenes/crear"; }
        try {
            long id=ordenes.crear(form);
            flash.addFlashAttribute("exito","Orden creada en estado PENDIENTE");
            return "redirect:/ordenes/"+id;
        } catch(IllegalArgumentException ex) {
            result.reject("orden.error",ex.getMessage());
            opciones(model);return "ordenes/crear";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {
            OrdenTrabajo orden=ordenes.obtener(id);
            model.addAttribute("orden",orden);
            model.addAttribute("serviciosAplicados",ordenes.serviciosDe(id));
            model.addAttribute("repuestosAplicados",ordenes.repuestosDe(id));
            model.addAttribute("catalogoServicios",servicios.listar());
            model.addAttribute("catalogoRepuestos",repuestos.listar());
            model.addAttribute("manoObra",ordenes.totalManoObra(id));
            model.addAttribute("repuestosTotal",ordenes.totalRepuestos(id));
            model.addAttribute("enProceso",orden.getEstado()==EstadoOrden.EN_PROCESO);
            return "ordenes/detalle";
        } catch(IllegalArgumentException ex) {
            flash.addFlashAttribute("error",ex.getMessage());return "redirect:/ordenes";
        }
    }

    @PostMapping("/{id}/iniciar")
    public String iniciar(@PathVariable long id,RedirectAttributes flash) {
        return accion(id,flash,()->ordenes.iniciar(id),"Orden iniciada");
    }

    @PostMapping("/{id}/servicios")
    public String agregarServicio(@PathVariable long id,@RequestParam long servicioId,RedirectAttributes flash) {
        return accion(id,flash,()->ordenes.agregarServicio(id,servicioId),"Servicio agregado");
    }

    @PostMapping("/{id}/servicios/{detalleId}/quitar")
    public String quitarServicio(@PathVariable long id,@PathVariable long detalleId,RedirectAttributes flash) {
        return accion(id,flash,()->ordenes.quitarServicio(id,detalleId),"Servicio retirado");
    }

    @PostMapping("/{id}/repuestos")
    public String agregarRepuesto(@PathVariable long id,@RequestParam long repuestoId,
            @RequestParam int cantidad,RedirectAttributes flash) {
        return accion(id,flash,()->ordenes.agregarRepuesto(id,repuestoId,cantidad),"Repuesto asignado y descontado del inventario");
    }

    @PostMapping("/{id}/repuestos/{detalleId}/quitar")
    public String quitarRepuesto(@PathVariable long id,@PathVariable long detalleId,RedirectAttributes flash) {
        return accion(id,flash,()->ordenes.quitarRepuesto(id,detalleId),"Repuesto devuelto al inventario");
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable long id,RedirectAttributes flash) {
        return accion(id,flash,()->ordenes.finalizar(id),"Orden finalizada. Comprobante disponible");
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable long id,RedirectAttributes flash) {
        return accion(id,flash,()->ordenes.cancelar(id),"Orden cancelada. Inventario reintegrado");
    }

    @GetMapping("/{id}/comprobante")
    public String comprobante(@PathVariable long id,Model model,RedirectAttributes flash) {
        try {
            OrdenTrabajo orden=ordenes.obtener(id);
            if (orden.getEstado()!=EstadoOrden.FINALIZADA)
                throw new IllegalArgumentException("El comprobante solo está disponible para órdenes finalizadas");
            model.addAttribute("orden",orden);
            model.addAttribute("serviciosAplicados",ordenes.serviciosDe(id));
            model.addAttribute("repuestosAplicados",ordenes.repuestosDe(id));
            return "ordenes/comprobante";
        } catch(IllegalArgumentException ex) {
            flash.addFlashAttribute("error",ex.getMessage());return "redirect:/ordenes/"+id;
        }
    }

    private String accion(long id,RedirectAttributes flash,Runnable tarea,String mensaje) {
        try {tarea.run();flash.addFlashAttribute("exito",mensaje);}
        catch(IllegalArgumentException ex) {flash.addFlashAttribute("error",ex.getMessage());}
        return "redirect:/ordenes/"+id;
    }
}
