package com.ultimoarranque.controller;

import com.ultimoarranque.model.EstadoOrden;
import com.ultimoarranque.model.reporte.ReporteTabla;
import com.ultimoarranque.service.MecanicoService;
import com.ultimoarranque.service.ReportePdfService;
import com.ultimoarranque.service.ReporteService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reportes;
    private final ReportePdfService pdf;
    private final MecanicoService mecanicos;

    public ReporteController(ReporteService reportes, ReportePdfService pdf,
            MecanicoService mecanicos) {
        this.reportes = reportes;
        this.pdf = pdf;
        this.mecanicos = mecanicos;
    }

    @GetMapping
    public String index(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("hoy", hoy);
        model.addAttribute("inicioSugerido", hoy.withDayOfMonth(1));
        model.addAttribute("mecanicos", mecanicos.listar());
        return "reportes/index";
    }

    @GetMapping("/pdf/ordenes-estado")
    @ResponseBody
    public ResponseEntity<byte[]> ordenesEstado(
            @RequestParam(required = false) String estado) throws IOException {
        EstadoOrden filtro = null;
        if (estado != null && !estado.isBlank()) {
            try {
                filtro = EstadoOrden.valueOf(estado.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido");
            }
        }
        return descargar(reportes.ordenesEstado(filtro), "ordenes_por_estado.pdf");
    }

    @GetMapping("/pdf/historial-vehiculo")
    @ResponseBody
    public ResponseEntity<byte[]> historial(
            @RequestParam(required = false) String placa) throws IOException {
        return descargar(reportes.historialVehiculo(placa), "historial_vehiculo.pdf");
    }

    @GetMapping("/pdf/inventario-bajo")
    @ResponseBody
    public ResponseEntity<byte[]> inventario() throws IOException {
        return descargar(reportes.inventarioBajo(), "inventario_bajo.pdf");
    }

    @GetMapping("/pdf/ordenes-mecanico")
    @ResponseBody
    public ResponseEntity<byte[]> ordenesMecanico(
            @RequestParam(required = false) Long mecanicoId) throws IOException {
        return descargar(reportes.ordenesMecanico(mecanicoId), "ordenes_por_mecanico.pdf");
    }

    @GetMapping("/pdf/ingresos")
    @ResponseBody
    public ResponseEntity<byte[]> ingresos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta)
            throws IOException {
        return descargar(reportes.ingresos(desde, hasta), "ingresos_por_reparaciones.pdf");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> errorFiltro(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    private ResponseEntity<byte[]> descargar(ReporteTabla tabla, String nombre) throws IOException {
        byte[] contenido = pdf.generar(tabla);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(contenido.length)
                .body(contenido);
    }
}
