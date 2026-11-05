package com.ultimoarranque.dao;

import com.ultimoarranque.util.ConexionBD;
import com.ultimoarranque.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAO {
    private static final String COLUMNAS = "id, identificador, nombre, telefono, correo, direccion";

    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM clientes ORDER BY id DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) clientes.add(desdeResultado(rs));
        }
        return clientes;
    }

    public Optional<Cliente> buscarPorId(long id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM clientes WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(desdeResultado(rs)) : Optional.empty();
            }
        }
    }

    public boolean identificadorExistente(String identificador, Long idExcluido) throws SQLException {
        String sql = "SELECT id FROM clientes WHERE identificador = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identificador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (idExcluido == null || rs.getLong("id") != idExcluido) return true;
                }
            }
        }
        return false;
    }

    public Cliente crear(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (identificador, nombre, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarCampos(stmt, cliente);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) cliente.setId(keys.getLong(1));
            }
        }
        return cliente;
    }

    public boolean actualizar(long id, Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET identificador = ?, nombre = ?, telefono = ?, correo = ?, direccion = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            asignarCampos(stmt, cliente);
            stmt.setLong(6, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean tieneVehiculos(long id) throws SQLException {
        String sql = "SELECT 1 FROM vehiculos WHERE cliente_id = ? LIMIT 1";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean eliminar(long id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private void asignarCampos(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getIdentificador());
        stmt.setString(2, cliente.getNombre());
        stmt.setString(3, cliente.getTelefono());
        stmt.setString(4, cliente.getCorreo());
        stmt.setString(5, cliente.getDireccion());
    }

    private Cliente desdeResultado(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setIdentificador(rs.getString("identificador"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setDireccion(rs.getString("direccion"));
        return cliente;
    }
}
