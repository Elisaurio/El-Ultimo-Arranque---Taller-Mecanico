package com.ultimoarranque.controller;

import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ultimoarranque.model.EstadoSistema;
import com.ultimoarranque.service.EstadoService;

@RestController
@RequestMapping("/api/estado")
public class EstadoController {
    private final EstadoService service;

    public EstadoController(EstadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EstadoSistema> consultar() {
        try {
            return ResponseEntity.ok(service.consultarEstado());
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new EstadoSistema("El Ultimo Arranque", "ACTIVO", "ERROR_CONEXION"));
        }
    }
}
