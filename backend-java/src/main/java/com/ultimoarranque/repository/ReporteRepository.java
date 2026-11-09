package com.ultimoarranque.repository;

import com.ultimoarranque.model.EstadoOrden;
import com.ultimoarranque.model.reporte.RepuestoBajo;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReporteRepository {

    private final JdbcTemplate jdbc;

    public ReporteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record OrdenEstado(long id, String estado, LocalDateTime fecha,
            String placa, String cliente, String mecanico, BigDecimal total) {}

    public record Historial(long id, LocalDateTime fecha, String estado,
            String mecanico, String problema, BigDecimal total) {}

    public record OrdenMecanico(long ordenId, String identificador,
            String mecanico, String estado, String placa, LocalDateTime fecha) {}

    public record Ingreso(long id, LocalDateTime fecha, String placa,
            String mecanico, BigDecimal manoObra, BigDecimal repuestos, BigDecimal total) {}

    public List<OrdenEstado> ordenesPorEstado(EstadoOrden estado) {
        String sql = "SELECT o.id,o.estado,o.fecha_creacion,v.placa,"
                + "c.nombre AS cliente,m.nombre AS mecanico,o.total "
                + "FROM ordenes_trabajo o "
                + "JOIN vehiculos v ON v.id=o.vehiculo_id "
                + "JOIN clientes c ON c.id=v.cliente_id "
                + "JOIN mecanicos m ON m.id=o.mecanico_id "
                + (estado == null ? "" : "WHERE o.estado=? ")
                + "ORDER BY o.estado,o.fecha_creacion DESC,o.id DESC";
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            if (estado != null) ps.setString(1, estado.name());
            return ps;
        }, (rs, row) -> new OrdenEstado(
                rs.getLong("id"),rs.getString("estado"),
                fecha(rs.getTimestamp("fecha_creacion")),
                rs.getString("placa"),rs.getString("cliente"),
                rs.getString("mecanico"),rs.getBigDecimal("total")));
    }

    public List<Historial> historialPorPlaca(String placa) {
        String sql = "SELECT o.id,o.fecha_creacion,o.estado,m.nombre AS mecanico,"
                + "o.problema,o.total FROM ordenes_trabajo o "
                + "JOIN vehiculos v ON v.id=o.vehiculo_id "
                + "JOIN mecanicos m ON m.id=o.mecanico_id "
                + "WHERE UPPER(v.placa)=UPPER(?) ORDER BY o.fecha_creacion DESC,o.id DESC";
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, placa);
            return ps;
        }, (rs, row) -> new Historial(
                rs.getLong("id"),fecha(rs.getTimestamp("fecha_creacion")),
                rs.getString("estado"),rs.getString("mecanico"),
                rs.getString("problema"),rs.getBigDecimal("total")));
    }

    public List<RepuestoBajo> inventarioBajo() {
        String sql = "SELECT codigo,nombre,existencia,precio FROM repuestos "
                + "WHERE existencia <= ? ORDER BY existencia ASC,nombre ASC";
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,5);
            return ps;
        }, (rs, row) -> new RepuestoBajo(rs.getString("codigo"),
                rs.getString("nombre"),rs.getInt("existencia"),rs.getBigDecimal("precio")));
    }

    public List<OrdenMecanico> ordenesPorMecanico(Long mecanicoId) {
        String sql = "SELECT o.id,m.identificador,m.nombre AS mecanico,"
                + "o.estado,v.placa,o.fecha_creacion FROM ordenes_trabajo o "
                + "JOIN mecanicos m ON m.id=o.mecanico_id "
                + "JOIN vehiculos v ON v.id=o.vehiculo_id "
                + (mecanicoId == null ? "" : "WHERE m.id=? ")
                + "ORDER BY m.nombre ASC,o.fecha_creacion DESC,o.id DESC";
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            if (mecanicoId != null) ps.setLong(1,mecanicoId);
            return ps;
        }, (rs, row) -> new OrdenMecanico(
                rs.getLong("id"),rs.getString("identificador"),
                rs.getString("mecanico"),rs.getString("estado"),
                rs.getString("placa"),fecha(rs.getTimestamp("fecha_creacion"))));
    }

    public List<Ingreso> ingresos(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT o.id,o.fecha_finalizacion,v.placa,"
                + "m.nombre AS mecanico,o.total_mano_obra,o.total_repuestos,o.total "
                + "FROM ordenes_trabajo o "
                + "JOIN vehiculos v ON v.id=o.vehiculo_id "
                + "JOIN mecanicos m ON m.id=o.mecanico_id "
                + "WHERE o.estado='FINALIZADA' "
                + "AND o.fecha_finalizacion >= ? "
                + "AND o.fecha_finalizacion < DATE_ADD(?, INTERVAL 1 DAY) "
                + "ORDER BY o.fecha_finalizacion ASC,o.id ASC";
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1,Date.valueOf(desde));
            ps.setDate(2,Date.valueOf(hasta));
            return ps;
        }, (rs, row) -> new Ingreso(
                rs.getLong("id"),fecha(rs.getTimestamp("fecha_finalizacion")),
                rs.getString("placa"),rs.getString("mecanico"),
                rs.getBigDecimal("total_mano_obra"),
                rs.getBigDecimal("total_repuestos"),rs.getBigDecimal("total")));
    }

    private static LocalDateTime fecha(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
