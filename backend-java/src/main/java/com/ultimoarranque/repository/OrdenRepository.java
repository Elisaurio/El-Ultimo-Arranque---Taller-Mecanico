package com.ultimoarranque.repository;

import com.ultimoarranque.model.EstadoOrden;
import com.ultimoarranque.model.OrdenTrabajo;
import com.ultimoarranque.model.OrdenServicioDetalle;
import com.ultimoarranque.model.OrdenRepuestoDetalle;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrdenRepository {
    private final JdbcTemplate jdbc;

    public OrdenRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String SELECT_DETALLE = "SELECT o.*,v.placa,m.nombre AS mecanico_nombre,c.nombre AS cliente_nombre "
            + "FROM ordenes_trabajo o JOIN vehiculos v ON v.id=o.vehiculo_id "
            + "JOIN mecanicos m ON m.id=o.mecanico_id JOIN clientes c ON c.id=v.cliente_id ";

    private final RowMapper<OrdenTrabajo> mapper = (rs, row) -> {
        OrdenTrabajo o = new OrdenTrabajo();
        o.setId(rs.getLong("id"));
        o.setVehiculoId(rs.getLong("vehiculo_id"));
        o.setMecanicoId(rs.getLong("mecanico_id"));
        o.setProblema(rs.getString("problema"));
        o.setEstado(EstadoOrden.valueOf(rs.getString("estado")));
        Timestamp creado = rs.getTimestamp("fecha_creacion");
        Timestamp terminado = rs.getTimestamp("fecha_finalizacion");
        o.setFechaCreacion(creado == null ? null : creado.toLocalDateTime());
        o.setFechaFinalizacion(terminado == null ? null : terminado.toLocalDateTime());
        o.setTotalManoObra(rs.getBigDecimal("total_mano_obra"));
        o.setTotalRepuestos(rs.getBigDecimal("total_repuestos"));
        o.setTotal(rs.getBigDecimal("total"));
        return o;
    };

    private final RowMapper<OrdenTrabajo> detalleMapper = (rs, row) -> {
        OrdenTrabajo o = mapper.mapRow(rs, row);
        o.setPlaca(rs.getString("placa"));
        o.setMecanicoNombre(rs.getString("mecanico_nombre"));
        o.setClienteNombre(rs.getString("cliente_nombre"));
        return o;
    };

    public List<OrdenTrabajo> listar() {
        return jdbc.query(con -> con.prepareStatement(SELECT_DETALLE + "ORDER BY o.id DESC"), detalleMapper);
    }

    public Optional<OrdenTrabajo> buscar(long id) {
        List<OrdenTrabajo> encontrados = jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(SELECT_DETALLE + "WHERE o.id=?");
            ps.setLong(1, id);
            return ps;
        }, detalleMapper);
        return encontrados.stream().findFirst();
    }

    public Optional<OrdenTrabajo> bloquear(long id) {
        List<OrdenTrabajo> encontrados = jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ordenes_trabajo WHERE id=? FOR UPDATE");
            ps.setLong(1, id);
            return ps;
        }, mapper);
        return encontrados.stream().findFirst();
    }

    public long crear(long vehiculoId, long mecanicoId, String problema) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO ordenes_trabajo (vehiculo_id,mecanico_id,problema,estado) VALUES (?,?,?,'PENDIENTE')",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, vehiculoId);
            ps.setLong(2, mecanicoId);
            ps.setString(3, problema);
            return ps;
        }, keys);
        if (keys.getKey() == null) throw new IllegalStateException("No se generó el número de orden");
        return keys.getKey().longValue();
    }

    public void iniciar(long id) {
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("UPDATE ordenes_trabajo SET estado='EN_PROCESO' WHERE id=?");
            ps.setLong(1,id); return ps;
        });
    }

    public void finalizar(long id, BigDecimal manoObra, BigDecimal repuestos) {
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("UPDATE ordenes_trabajo SET estado='FINALIZADA',fecha_finalizacion=NOW(),total_mano_obra=?,total_repuestos=?,total=? WHERE id=?");
            ps.setBigDecimal(1,manoObra);
            ps.setBigDecimal(2,repuestos);
            ps.setBigDecimal(3,manoObra.add(repuestos));
            ps.setLong(4,id);return ps;
        });
    }

    public void cancelar(long id) {
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("UPDATE ordenes_trabajo SET estado='CANCELADA' WHERE id=?");
            ps.setLong(1,id);return ps;
        });
    }

    public List<OrdenServicioDetalle> listarServicios(long ordenId) {
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT os.id,os.servicio_id,s.descripcion,os.costo_aplicado FROM orden_servicios os JOIN servicios s ON s.id=os.servicio_id WHERE os.orden_id=? ORDER BY os.id");
            ps.setLong(1,ordenId);return ps;
        }, (rs,row)->{
            OrdenServicioDetalle d = new OrdenServicioDetalle();
            d.setId(rs.getLong("id"));d.setServicioId(rs.getLong("servicio_id"));
            d.setDescripcion(rs.getString("descripcion"));d.setCostoAplicado(rs.getBigDecimal("costo_aplicado"));
            return d;
        });
    }

    public List<OrdenRepuestoDetalle> listarRepuestos(long ordenId) {
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT r.id,r.repuesto_id,p.nombre,r.cantidad,r.precio_unitario_aplicado FROM orden_repuestos r JOIN repuestos p ON p.id=r.repuesto_id WHERE r.orden_id=? ORDER BY r.id");
            ps.setLong(1,ordenId);return ps;
        }, (rs,row)->{
            OrdenRepuestoDetalle d = new OrdenRepuestoDetalle();
            d.setId(rs.getLong("id"));d.setRepuestoId(rs.getLong("repuesto_id"));
            d.setNombre(rs.getString("nombre"));d.setCantidad(rs.getInt("cantidad"));
            d.setPrecioUnitarioAplicado(rs.getBigDecimal("precio_unitario_aplicado"));return d;
        });
    }

    public void agregarServicio(long ordenId,long servicioId,BigDecimal costo) {
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO orden_servicios (orden_id,servicio_id,costo_aplicado) VALUES (?,?,?)");
            ps.setLong(1,ordenId);ps.setLong(2,servicioId);ps.setBigDecimal(3,costo);return ps;
        });
    }

    public int quitarServicio(long ordenId,long detalleId) {
        return jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM orden_servicios WHERE orden_id=? AND id=?");
            ps.setLong(1,ordenId);ps.setLong(2,detalleId);return ps;
        });
    }

    public void agregarRepuesto(long ordenId,long repuestoId,int cantidad,BigDecimal precio) {
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO orden_repuestos (orden_id,repuesto_id,cantidad,precio_unitario_aplicado) VALUES (?,?,?,?)");
            ps.setLong(1,ordenId);ps.setLong(2,repuestoId);ps.setInt(3,cantidad);ps.setBigDecimal(4,precio);return ps;
        });
    }

    public int quitarRepuesto(long ordenId,long detalleId) {
        return jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM orden_repuestos WHERE orden_id=? AND id=?");
            ps.setLong(1,ordenId);ps.setLong(2,detalleId);return ps;
        });
    }

    public int contarServicios(long ordenId) {
        Integer count = jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT COUNT(*) FROM orden_servicios WHERE orden_id=?");
            ps.setLong(1,ordenId);return ps;
        },rs->rs.next()?rs.getInt(1):0);
        return count == null ? 0 : count;
    }

    public BigDecimal totalServicios(long ordenId) {
        BigDecimal sum = jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT COALESCE(SUM(costo_aplicado),0) FROM orden_servicios WHERE orden_id=?");
            ps.setLong(1,ordenId);return ps;
        },rs->rs.next()?rs.getBigDecimal(1):BigDecimal.ZERO);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    public BigDecimal totalRepuestos(long ordenId) {
        BigDecimal sum = jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT COALESCE(SUM(cantidad*precio_unitario_aplicado),0) FROM orden_repuestos WHERE orden_id=?");
            ps.setLong(1,ordenId);return ps;
        },rs->rs.next()?rs.getBigDecimal(1):BigDecimal.ZERO);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    public int contar() {
        Integer cuenta = jdbc.query(con -> con.prepareStatement("SELECT COUNT(*) FROM ordenes_trabajo"), rs -> rs.next()?rs.getInt(1):0);
        return cuenta == null ? 0 : cuenta;
    }
}
