package com.ultimoarranque.service;

import java.sql.SQLException;
import org.springframework.stereotype.Service;
import com.ultimoarranque.model.EstadoSistema;
import com.ultimoarranque.repository.EstadoRepository;

@Service
public class EstadoService {
    private final EstadoRepository repository;

    public EstadoService(EstadoRepository repository) {
        this.repository = repository;
    }

    public EstadoSistema consultarEstado() throws SQLException {
        boolean conectada = repository.verificarConexion();
        return new EstadoSistema("El Ultimo Arranque", "ACTIVO", conectada ? "CONECTADA" : "DESCONECTADA");
    }
}
