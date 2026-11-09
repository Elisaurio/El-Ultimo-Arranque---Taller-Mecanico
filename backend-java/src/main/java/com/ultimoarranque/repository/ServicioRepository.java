package com.ultimoarranque.repository;
import com.ultimoarranque.model.Servicio;
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
public class ServicioRepository {
    private final JdbcTemplate jdbc;
    public ServicioRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    private final RowMapper<Servicio> mapper = (rs, row) -> {
        Servicio obj = new Servicio();
        obj.setId(rs.getLong("id"));
        obj.setCodigo(rs.getString("codigo"));
        obj.setDescripcion(rs.getString("descripcion"));
        obj.setCostoManoObra(rs.getBigDecimal("costo_mano_obra"));
        return obj;
    };
    public List<Servicio> listar() {
        return jdbc.query(con -> con.prepareStatement("SELECT id,codigo,descripcion,costo_mano_obra FROM servicios ORDER BY id DESC"), mapper);
    }
    public Optional<Servicio> buscar(long id) {
        List<Servicio> lista=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT id,codigo,descripcion,costo_mano_obra FROM servicios WHERE id=?");
            ps.setLong(1,id);return ps;
        }, mapper);
        return lista.stream().findFirst();
    }
    public boolean duplicado(String value, Long exceptId) {
        Integer count=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT COUNT(*) FROM servicios WHERE codigo=? AND (? IS NULL OR id<>?)");
            ps.setString(1,value);
            if(exceptId==null) {ps.setNull(2,java.sql.Types.BIGINT);ps.setNull(3,java.sql.Types.BIGINT);}
            else {ps.setLong(2,exceptId);ps.setLong(3,exceptId);}
            return ps;
        },rs->rs.next()?rs.getInt(1):0);
        return count!=null && count>0;
    }
    public void crear(Servicio obj) {
        KeyHolder key=new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("INSERT INTO servicios (codigo,descripcion,costo_mano_obra) VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, obj.getCodigo());
            ps.setString(2, obj.getDescripcion());
            ps.setBigDecimal(3, obj.getCostoManoObra());
            return ps;
        },key);
    }
    public int actualizar(Servicio obj) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("UPDATE servicios SET codigo=?,descripcion=?,costo_mano_obra=? WHERE id=?");
            ps.setString(1, obj.getCodigo());
            ps.setString(2, obj.getDescripcion());
            ps.setBigDecimal(3, obj.getCostoManoObra());
            ps.setLong(4,obj.getId());return ps;
        });
    }
    public int eliminar(long id) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("DELETE FROM servicios WHERE id=?");
            ps.setLong(1,id);return ps;
        });
    }
}
