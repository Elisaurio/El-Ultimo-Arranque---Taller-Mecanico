package com.ultimoarranque.repository;

import java.sql.PreparedStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RepuestoExistenciaRepository {
    private final JdbcTemplate jdbc;
    public RepuestoExistenciaRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    public boolean descontarSiDisponible(long repuestoId, int cantidad) {
        int filas = jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE repuestos SET existencia=existencia-? WHERE id=? AND existencia>=?");
            ps.setInt(1,cantidad); ps.setLong(2,repuestoId); ps.setInt(3,cantidad);
            return ps;
        });
        return filas == 1;
    }

    public void reintegrar(long repuestoId, int cantidad) {
        int filas = jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE repuestos SET existencia=existencia+? WHERE id=? AND existencia <= 2147483647 - ?");
            ps.setInt(1,cantidad); ps.setLong(2,repuestoId); ps.setInt(3,cantidad);
            return ps;
        });
        if (filas != 1) throw new IllegalArgumentException("No se pudo devolver el repuesto al inventario");
    }
}
