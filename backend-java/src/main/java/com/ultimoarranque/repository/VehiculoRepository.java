package com.ultimoarranque.repository;
import com.ultimoarranque.model.Vehiculo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class VehiculoRepository {
    private final JdbcTemplate jdbc;
    public VehiculoRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    private final RowMapper<Vehiculo> mapper = (rs, row) -> {
        Vehiculo obj = new Vehiculo();
        obj.setId(rs.getLong("id"));
        obj.setClienteId(rs.getObject("cliente_id", Long.class));
        obj.setPlaca(rs.getString("placa"));
        obj.setMarca(rs.getString("marca"));
        obj.setModelo(rs.getString("modelo"));
        obj.setAnio(rs.getObject("anio", Integer.class));
        obj.setColor(rs.getString("color"));
        return obj;
    };
    public List<Vehiculo> listar() {
        return jdbc.query(con -> con.prepareStatement("SELECT id,cliente_id,placa,marca,modelo,anio,color FROM vehiculos ORDER BY id DESC"), mapper);
    }
    public Optional<Vehiculo> buscar(long id) {
        List<Vehiculo> lista=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT id,cliente_id,placa,marca,modelo,anio,color FROM vehiculos WHERE id=?");
            ps.setLong(1,id);return ps;
        }, mapper);
        return lista.stream().findFirst();
    }
    public boolean duplicado(String value, Long exceptId) {
        Integer count=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT COUNT(*) FROM vehiculos WHERE placa=? AND (? IS NULL OR id<>?)");
            ps.setString(1,value);
            if(exceptId==null) {ps.setNull(2,java.sql.Types.BIGINT);ps.setNull(3,java.sql.Types.BIGINT);}
            else {ps.setLong(2,exceptId);ps.setLong(3,exceptId);}
            return ps;
        },rs->rs.next()?rs.getInt(1):0);
        return count!=null && count>0;
    }
    public void crear(Vehiculo obj) {
        KeyHolder key=new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("INSERT INTO vehiculos (cliente_id,placa,marca,modelo,anio,color) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, obj.getClienteId(), java.sql.Types.BIGINT);
            ps.setString(2, obj.getPlaca());
            ps.setString(3, obj.getMarca());
            ps.setString(4, obj.getModelo());
            ps.setObject(5, obj.getAnio(), java.sql.Types.INTEGER);
            ps.setString(6, obj.getColor());
            return ps;
        },key);
    }
    public int actualizar(Vehiculo obj) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("UPDATE vehiculos SET cliente_id=?,placa=?,marca=?,modelo=?,anio=?,color=? WHERE id=?");
            ps.setObject(1, obj.getClienteId(), java.sql.Types.BIGINT);
            ps.setString(2, obj.getPlaca());
            ps.setString(3, obj.getMarca());
            ps.setString(4, obj.getModelo());
            ps.setObject(5, obj.getAnio(), java.sql.Types.INTEGER);
            ps.setString(6, obj.getColor());
            ps.setLong(7,obj.getId());return ps;
        });
    }
    public int eliminar(long id) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("DELETE FROM vehiculos WHERE id=?");
            ps.setLong(1,id);return ps;
        });
    }
    public List<Vehiculo> porCliente(long clienteId) {
        return jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT id,cliente_id,placa,marca,modelo,anio,color FROM vehiculos WHERE cliente_id=? ORDER BY id DESC");
            ps.setLong(1,clienteId);return ps;
        }, mapper);
    }
    public List<java.util.Map<String,Object>> historial(long id) {
        return jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT id,estado,fecha_creacion,problema,total FROM ordenes_trabajo WHERE vehiculo_id=? ORDER BY fecha_creacion DESC");
            ps.setLong(1,id);return ps;
        },(rs,row) -> {
            java.util.Map<String,Object> dato=new java.util.LinkedHashMap<>();
            dato.put("id",rs.getLong("id"));dato.put("estado",rs.getString("estado"));
            dato.put("fecha",rs.getTimestamp("fecha_creacion"));
            dato.put("problema",rs.getString("problema"));dato.put("total",rs.getBigDecimal("total"));
            return dato;
        });
    }
}
