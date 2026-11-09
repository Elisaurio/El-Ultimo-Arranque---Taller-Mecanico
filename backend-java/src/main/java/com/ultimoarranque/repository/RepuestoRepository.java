package com.ultimoarranque.repository;
import com.ultimoarranque.model.Repuesto;
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
public class RepuestoRepository {
    private final JdbcTemplate jdbc;
    public RepuestoRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    private final RowMapper<Repuesto> mapper = (rs, row) -> {
        Repuesto obj = new Repuesto();
        obj.setId(rs.getLong("id"));
        obj.setCodigo(rs.getString("codigo"));
        obj.setNombre(rs.getString("nombre"));
        obj.setDescripcion(rs.getString("descripcion"));
        obj.setPrecio(rs.getBigDecimal("precio"));
        obj.setExistencia(rs.getObject("existencia", Integer.class));
        return obj;
    };
    public List<Repuesto> listar() {
        return jdbc.query(con -> con.prepareStatement("SELECT id,codigo,nombre,descripcion,precio,existencia FROM repuestos ORDER BY id DESC"), mapper);
    }
    public Optional<Repuesto> buscar(long id) {
        List<Repuesto> lista=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT id,codigo,nombre,descripcion,precio,existencia FROM repuestos WHERE id=?");
            ps.setLong(1,id);return ps;
        }, mapper);
        return lista.stream().findFirst();
    }
    public boolean duplicado(String value, Long exceptId) {
        Integer count=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT COUNT(*) FROM repuestos WHERE codigo=? AND (? IS NULL OR id<>?)");
            ps.setString(1,value);
            if(exceptId==null) {ps.setNull(2,java.sql.Types.BIGINT);ps.setNull(3,java.sql.Types.BIGINT);}
            else {ps.setLong(2,exceptId);ps.setLong(3,exceptId);}
            return ps;
        },rs->rs.next()?rs.getInt(1):0);
        return count!=null && count>0;
    }
    public void crear(Repuesto obj) {
        KeyHolder key=new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("INSERT INTO repuestos (codigo,nombre,descripcion,precio,existencia) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, obj.getCodigo());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setBigDecimal(4, obj.getPrecio());
            ps.setObject(5, obj.getExistencia(), java.sql.Types.INTEGER);
            return ps;
        },key);
    }
    public int actualizar(Repuesto obj) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("UPDATE repuestos SET codigo=?,nombre=?,descripcion=?,precio=? WHERE id=?");
            ps.setString(1, obj.getCodigo());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setBigDecimal(4, obj.getPrecio());
            ps.setLong(5,obj.getId());return ps;
        });
    }
    public int eliminar(long id) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("DELETE FROM repuestos WHERE id=?");
            ps.setLong(1,id);return ps;
        });
    }
    public int abastecer(long id,int cantidad) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("UPDATE repuestos SET existencia=existencia+? WHERE id=? AND existencia<=2147483647-?");
            ps.setInt(1,cantidad);ps.setLong(2,id);ps.setInt(3,cantidad);return ps;
        });
    }
    public List<Repuesto> inventarioBajo() {
        return jdbc.query(con -> con.prepareStatement("SELECT id,codigo,nombre,descripcion,precio,existencia FROM repuestos WHERE existencia<=5 ORDER BY existencia ASC,id DESC"),mapper);
    }
}
