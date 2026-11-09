package com.ultimoarranque.repository;

import com.ultimoarranque.model.Cliente;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepository {
    private final JdbcTemplate jdbc;

    public ClienteRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<Cliente> mapper = (rs, rowNum) -> {
        Cliente c = new Cliente();
        c.setId(rs.getLong("id"));
        c.setIdentificador(rs.getString("identificador"));
        c.setNombre(rs.getString("nombre"));
        c.setTelefono(rs.getString("telefono"));
        c.setCorreo(rs.getString("correo"));
        c.setDireccion(rs.getString("direccion"));
        return c;
    };

    public List<Cliente> listar() {
        return jdbc.query(con -> con.prepareStatement(
            "SELECT id,identificador,nombre,telefono,correo,direccion FROM clientes ORDER BY id DESC"), mapper);
    }

    public Optional<Cliente> buscarPorId(long id) {
        List<Cliente> encontrados = jdbc.query(
            con -> { PreparedStatement ps = con.prepareStatement("SELECT id,identificador,nombre,telefono,correo,direccion FROM clientes WHERE id=?"); ps.setLong(1, id); return ps; }, mapper);
        return encontrados.stream().findFirst();
    }

    public boolean existeIdentificador(String identificador, Long exceptoId) {
        Integer cuenta = jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM clientes WHERE identificador=? AND (? IS NULL OR id<>?)");
            ps.setString(1, identificador);
            if (exceptoId == null) { ps.setNull(2, java.sql.Types.BIGINT); ps.setNull(3, java.sql.Types.BIGINT); }
            else { ps.setLong(2, exceptoId); ps.setLong(3, exceptoId); }
            return ps;
        }, rs -> rs.next() ? rs.getInt(1) : 0);
        return cuenta != null && cuenta > 0;
    }

    public long crear(Cliente c) {
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO clientes (identificador,nombre,telefono,correo,direccion) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getIdentificador()); ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono()); ps.setString(4, c.getCorreo()); ps.setString(5, c.getDireccion());
            return ps;
        }, key);
        if (key.getKey() == null) throw new IllegalStateException("No se generó ID de cliente");
        return key.getKey().longValue();
    }

    public int actualizar(Cliente c) {
        return jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("UPDATE clientes SET identificador=?,nombre=?,telefono=?,correo=?,direccion=? WHERE id=?");
            ps.setString(1,c.getIdentificador()); ps.setString(2,c.getNombre());
            ps.setString(3,c.getTelefono()); ps.setString(4,c.getCorreo());
            ps.setString(5,c.getDireccion()); ps.setLong(6,c.getId()); return ps;
        });
    }

    public boolean tieneVehiculos(long clienteId) {
        Integer count = jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM vehiculos WHERE cliente_id=?");
            ps.setLong(1, clienteId); return ps;
        }, rs -> rs.next() ? rs.getInt(1) : 0);
        return count != null && count > 0;
    }

    public int eliminar(long id) {
        return jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM clientes WHERE id=?"); ps.setLong(1,id); return ps;
        });
    }

    public int contar() {
        Integer count = jdbc.query(con -> con.prepareStatement("SELECT COUNT(*) FROM clientes"),
            rs -> rs.next() ? rs.getInt(1) : 0);
        return count == null ? 0 : count;
    }
}
