package com.ultimoarranque.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class EstadoRepository {
    private final DataSource dataSource;

    public EstadoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean verificarConexion() throws SQLException {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT 1");
             ResultSet resultado = sentencia.executeQuery()) {
            return resultado.next() && resultado.getInt(1) == 1;
        }
    }
}
