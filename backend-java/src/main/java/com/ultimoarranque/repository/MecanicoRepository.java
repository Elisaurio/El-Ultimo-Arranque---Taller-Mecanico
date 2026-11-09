package com.ultimoarranque.repository;
import com.ultimoarranque.model.Mecanico;
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
public class MecanicoRepository {
    private final JdbcTemplate jdbc;
    public MecanicoRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    private final RowMapper<Mecanico> mapper = (rs, row) -> {
        Mecanico obj = new Mecanico();
        obj.setId(rs.getLong("id"));
        obj.setIdentificador(rs.getString("identificador"));
        obj.setNombre(rs.getString("nombre"));
        obj.setTelefono(rs.getString("telefono"));
        obj.setEspecialidad(rs.getString("especialidad"));
        return obj;
    };
    public List<Mecanico> listar() {
        return jdbc.query(con -> con.prepareStatement("SELECT id,identificador,nombre,telefono,especialidad FROM mecanicos ORDER BY id DESC"), mapper);
    }
    public Optional<Mecanico> buscar(long id) {
        List<Mecanico> lista=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT id,identificador,nombre,telefono,especialidad FROM mecanicos WHERE id=?");
            ps.setLong(1,id);return ps;
        }, mapper);
        return lista.stream().findFirst();
    }
    public boolean duplicado(String value, Long exceptId) {
        Integer count=jdbc.query(con -> {
            PreparedStatement ps=con.prepareStatement("SELECT COUNT(*) FROM mecanicos WHERE identificador=? AND (? IS NULL OR id<>?)");
            ps.setString(1,value);
            if(exceptId==null) {ps.setNull(2,java.sql.Types.BIGINT);ps.setNull(3,java.sql.Types.BIGINT);}
            else {ps.setLong(2,exceptId);ps.setLong(3,exceptId);}
            return ps;
        },rs->rs.next()?rs.getInt(1):0);
        return count!=null && count>0;
    }
    public void crear(Mecanico obj) {
        KeyHolder key=new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("INSERT INTO mecanicos (identificador,nombre,telefono,especialidad) VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, obj.getIdentificador());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTelefono());
            ps.setString(4, obj.getEspecialidad());
            return ps;
        },key);
    }
    public int actualizar(Mecanico obj) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("UPDATE mecanicos SET identificador=?,nombre=?,telefono=?,especialidad=? WHERE id=?");
            ps.setString(1, obj.getIdentificador());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTelefono());
            ps.setString(4, obj.getEspecialidad());
            ps.setLong(5,obj.getId());return ps;
        });
    }
    public int eliminar(long id) {
        return jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement("DELETE FROM mecanicos WHERE id=?");
            ps.setLong(1,id);return ps;
        });
    }
}
