package com.ultimoarranque.controller;

import com.ultimoarranque.model.reporte.RepuestoBajo;
import com.ultimoarranque.service.ReporteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteApiController {

    private final ReporteService reportes;

    public ReporteApiController(ReporteService reportes) {
        this.reportes = reportes;
    }

    @GetMapping("/inventario-bajo")
    public List<RepuestoBajo> inventarioBajo() {
        return reportes.inventarioBajoDatos();
    }
}
